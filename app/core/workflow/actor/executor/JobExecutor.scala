package core.workflow.actor.executor

import akka.actor.{Actor}
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import config.Config
import core.types.JS_Types._
import core.workflow.WorkflowCollection
import core.workflow.actor.message._
import core.workflow.dataflow.node.JobDescription
import core.workflow.actor.{ActorManager, CachedThreadPoolWorkflowDispatcher}
import core.exception.EntryNotFoundException
import models.JobQueueDB

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/5/12
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 */
class JobExecutor(override val ref: JobDescription)
  extends Actor with Executor[JobDescription] with SimpleExecutorCleanUpProcedure[JobDescription] {
  implicit val ec = CachedThreadPoolWorkflowDispatcher.getExecutionContext
  val value = Promise[String]
  override def receive = {
    case StartMessage() =>
      markThisNodeAsStarted()
      notifyNonSupervisorParent(self, context)

      // TODO: is there any situation where the parameter is filled right after it starts? yes, in case that it doesn't have parameter
      if (isParameterMapPopulated()) {
        JobQueueDB.enqueueJob(ref.userId, ref.parentWorkflowId.get, ref.nodeId, ref.generateXml())
      }

    case ParamInputMessage(key, value) =>
      tryAssignParamValue(key, value)
      if (isParameterMapPopulated()) {
        JobQueueDB.enqueueJob(ref.userId, ref.parentWorkflowId.get, ref.nodeId, ref.generateXml())
      }

    case WorkerResultMessage(value) =>
      this.value.success(value)
      val supervisedChildren = startSupervisedChildren(context)
      sendResultToSupervisedChildren(supervisedChildren, value)
      for ((ref, key, node) <- supervisedChildren) {
        areResultsSent(node.nodeId) = 1
      }
      performSimpleNodeCleanUpProcedure(value)

    case RequestResultMessage(senderRef, childNodeId) =>
      var key: Option[String] = None
      for (pair <- ref.outgoing) {
        if (pair._1.nodeId == childNodeId) {
          key = Some(pair._2)
        }
      }
      if (value.isCompleted) {
        val value = Await.result(this.value.future, 1 seconds)
        senderRef ! ParamInputMessage(key.get, value)
        areResultsSent(childNodeId) = 1
        performSimpleNodeCleanUpProcedure(value)
      }
      else {
        senderRef ! ResultNotReadyMessage()
      }
    case ResultNotReadyMessage() =>
      ActorManager.rootActorSystem.scheduler.scheduleOnce(Config.retryInCaseResultNotReadyFrequency, sender, RequestResultMessage(self, ref.nodeId))

    case KeepAliveMessage() =>

    case WorkerTimeoutMessage() =>

  }
  private def tryAssignParamValue(key: String, value: String) = {
    val opt = ref.parameterMap.get
    opt.get(key) match {
      case Some(x) =>
        val paramtype = opt(key)._2
        opt(key) = (Some(value), paramtype)
        true
      case None =>
        throw new EntryNotFoundException("parameter key not found")
        false
    }
  }
  private def isParameterMapPopulated() = {
    // TODO
    def selectEmptyParam(arg: (String, (Option[String], JS))) = {
      arg._2._1 match { // target the Option[String] part of "arg"
        case None =>
          true // returns true if param element is empty
        case Some(str) =>
          false
      }
    }
    ref.parameterMap match {
      case None =>
        true
      case Some(x) =>
        val empty = x.filter(selectEmptyParam)
        empty.size == 0
    }
  }
}
