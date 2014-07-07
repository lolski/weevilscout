package controllers

import play.api.mvc._
import java.io._
import models._
import models.helper.fileio._
import java.util.UUID
import org.apache.commons.lang.StringUtils
import core.workflow.WorkflowCollection
import core.workflow.actor.ActorManager
import core.workflow.actor.message.{WorkerResultMessage}
import core.exception.EntryNotFoundException
import core.workflow.dataflow.node.{ReducerDescription, JobDescription}

object JobFrontend extends Controller {
  /*
   * this would be used to protect the workflow and job submission page from users
   * the workflow and job submission page should only be available to administrators
   */
  val passwd = "weevil"

  /*
  * shows the index page with the big red button
  */
  def index = Action {
    req =>
      Ok(views.html.index())
  }
  def getstore(id: String) = Action {
    val content = new File("public/job/");

    Ok(views.xml.getstore(content)).as("text/xml")
  }

  /*
  * this method is called whenever a user that is connected calls to the /dequeue page
  * or in other words, when a user requests a job to be performed
  */
  def dequeue(id: String) = Action {
    req =>
      val useragent = req.headers.get("user-agent").get
      val userip = req.remoteAddress
      val webcl = req.queryString("webcl")(0)

      val isWebCLCapable =
        if (webcl == "true") {
          true
        }
        else {
          false
        }
      val xml_out = JobQueueDB.dequeueJob(useragent, userip, isWebCLCapable)
      val workflowIdElem = xml_out \\ "workflow-id"
      if (workflowIdElem.size > 0) { // if xml_out != <sleep>10</sleep>
      val workflowId = UUID.fromString(workflowIdElem.text)
        val workflow = WorkflowCollection.findWorkflowInstance(workflowId)
        workflow match {
          case Some(w) =>
            val jobId = UUID.fromString((xml_out \\ "job-id").text)
            val whichJob = w.findValueInstance(jobId)
            whichJob match {
              case Some(j) =>
                if (j.isInstanceOf[JobDescription]) {
                  w.eventHandlersMonitor.onJobExecutorDequeued(j.asInstanceOf[JobDescription])
                }
                else if (j.isInstanceOf[ReducerDescription]) {
                  w.eventHandlersMonitor.onReducerExecutorDequeued(j.asInstanceOf[ReducerDescription])
                }
              case None =>
                // wtf
                throw new EntryNotFoundException("job not found")
            }

          case None =>
            // wtf
            throw new EntryNotFoundException("workflow not found")
        }
      }

      Ok(views.xml.xmlcontainer(xml_out)).as("text/xml")
  }

  /*
  * this method is called whenever an administrator submits a job
  */
  def enqueue() = Action {
    req =>
      val userId = req.body.asFormUrlEncoded.get("id")(0)
      val weevil = req.body.asFormUrlEncoded.get("weevil")(0)
      /*`
      val weevil =
        <weevil>
      		<name>abc</name>
      		<parameters>
      			<a >
      				<a.0>13</a.0>
        		</a>
        		<b >
         			<b.0>13</b.0>
       			</b>
       		</parameters>
       		<source>{"function weevil_main(a, b){ return a+b; }"}</source>
        </weevil>
       */
      val weevil_xml = scala.xml.XML.loadString(weevil.toString)

      JobQueueDB.enqueueJob(userId=UUID.fromString(userId), jobDescription=weevil_xml);
      Ok
  }

  /*
  * this method is called regularly by user from the /heartbeat url.
  * the frequency of invocations is regulated in one of the javascript file
  */
  def heartbeat() = Action {
    req =>
      val id = req.queryString("id")(0)
      val latlog = req.queryString("lat")(0) + ":" + req.queryString("log")(0)
      val flops = req.queryString("flops")(0)
      val webcl = req.queryString("webcl")(0)
      val uagent = req.headers.get("user-agent")
      val isWebCLCapable =
        if (webcl == "true") {
          true
        }
        else {
          false
        }
      val ip = req.remoteAddress
      // TODO: uagent.toString --> uagent.get
      val hb = Geolocations.heartbeat(id, latlog, flops, uagent.toString(), ip, isWebCLCapable, false)
      val xmlout = hb
      Ok(views.xml.xmlcontainer(xmlout)).as("text/xml")
  }

  def submit = Action {
    req =>
      val id = req.body.asFormUrlEncoded.get("id")(0) // TODO: change to user_id
    val result = req.body.asFormUrlEncoded.get("result")(0) // encode uricomponent([error] --> what?? worker-core.js line 80
    val jobId = req.body.asFormUrlEncoded.get("job_id")(0)
      val workflowId = req.body.asFormUrlEncoded.get("workflow_id")(0) // TODO:
    val jobName = StringUtils.chomp(req.body.asFormUrlEncoded.get("job_name")(0))
      val status = req.body.asFormUrlEncoded.get("status")(0)

      // find on which WorkflowInstance a Value belongs to, then complete the promise of that value
      val workflow = WorkflowCollection.findWorkflowInstance(UUID.fromString(workflowId)) // TODO: add workflow instance
      workflow match {
        case Some(w) =>
          val whichJob = w.findValueInstance(UUID.fromString(jobId))
          whichJob match {
            case Some(j) =>
              val actorPath = core.workflow.actor.getActorPath(j.actorPath)
              val actorRef = ActorManager.rootActorSystem.actorFor(actorPath)
              actorRef ! WorkerResultMessage(result)
              if (j.isInstanceOf[JobDescription]) {
                w.eventHandlersMonitor.onJobExecutorResultReceived(j.asInstanceOf[JobDescription], result)
              }
              else if (j.isInstanceOf[ReducerDescription]) {
                w.eventHandlersMonitor.onReducerExecutorResultReceived(j.asInstanceOf[ReducerDescription], result)
              }
            case None =>
              // wtf
              throw new EntryNotFoundException("job not found")
          }

        case None =>
          // wtf
          throw new EntryNotFoundException("workflow not found")
      }
      //TODO: check this construct below

      writeWorkflowResult(workflowId, jobId, result)
      JobQueueDB.submitJobInAWorkflow(workflowId, jobId, status.toInt)
      // put result
      Ok(views.xml.xmlcontainer(<root>
        <status>OK</status>
      </root>)).as("text/xml")
  }

  def submit_noworkflow = Action {
    req =>
      val id = req.body.asFormUrlEncoded.get("id")(0)
      val result = req.body.asFormUrlEncoded.get("result")(0) // encode uricomponent([error] --> what?? worker-core.js line 80
    val job_id = req.body.asFormUrlEncoded.get("job_id")(0)
      val job_name = org.apache.commons.lang.StringUtils.chomp(req.body.asFormUrlEncoded.get("job_name")(0))
      val status = req.body.asFormUrlEncoded.get("status")(0)

      writeJobResult(job_id, job_name, result)
      JobQueueDB.submitJobWithNoWorkflow(job_id, status.toInt)
      // put result
      Ok(views.xml.xmlcontainer(<root><status>OK</status></root>)).as("text/xml")
  }
  def clearAll = Action {
    req =>
      var ret = <msg></msg>
      val secret = true //req.queryString("secret")(0)
      if (true) {
        Geolocations.truncate
        JobQueueDB.truncate
        ret = <msg>cleared</msg>
      }
      else {
        ret = <msg>not authorized</msg>
      }
      Ok(views.xml.xmlcontainer(<root>
        {ret}
      </root>)).as("text/xml")
  }

  def end(id: String) = Action {
    req =>
      Geolocations.setClientStateDeactivated(id)
      Ok
  }
}
