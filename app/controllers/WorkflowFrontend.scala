package controllers

import play.api.mvc._
import java.io.File
import core.workflow.WorkflowCollection
import java.util.UUID

object WorkflowFrontend extends Controller {
  /*
  * get the list of jobs that's available on the "weevils" directory
  */
  def getstore(id: String) = Action {
    val content = new File("public/workflow/");

    Ok(views.xml.getworkflowstore(content)).as("text/xml")
  }
  def enqueue() = Action {
    req =>
      val agentId = req.body.asFormUrlEncoded.get("id")(0)
      val workflow = scala.xml.XML.loadString(req.body.asFormUrlEncoded.get("workflow")(0))

      //TODO: execute
      WorkflowCollection.launch(UUID.fromString(agentId), workflow)

      Ok
  }
}
