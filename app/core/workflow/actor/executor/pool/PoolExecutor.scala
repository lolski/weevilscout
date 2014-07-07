package core.workflow.actor.executor.pool

import akka.actor.{Props, Actor}
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import core.workflow.actor.message._
import core.workflow.dataflow.node.pool.PoolDescription
import core.workflow.actor.executor.{MetaExecutorCleanUpProcedure, ValueExecutor, Executor}
import core.workflow.actor.{CachedThreadPoolWorkflowDispatcher, ActorManager}
import config.Config

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/21/12
 * Time: 2:29 AM
 * To change this template use File | Settings | File Templates.
 */
class PoolExecutor(override val ref: PoolDescription)
  extends Actor with Executor[PoolDescription] with MetaExecutorCleanUpProcedure[PoolDescription] {
  implicit val ec = CachedThreadPoolWorkflowDispatcher.getExecutionContext
  val value = Promise[String]
  def receive = {
    case StartMessage() =>
      markThisNodeAsStarted()
      notifyNonSupervisorParent(self, context)
    case ParamInputMessage(key, value) =>
      // forward this to init node
      ref.initialize(value)
      start()

    case WorkerResultMessage(value) =>
      this.value.success(value)

      // reducer should send his result here
      val supervisedChildren = startSupervisedChildren(context)
      sendResultToSupervisedChildren(supervisedChildren, value)
      for ((ref, key, node) <- supervisedChildren) {
        areResultsSent(node.nodeId) = 1
      }
      // TODO: if this is the final node
      performMetaNodeCleanUpProcedure(value)
    case RequestResultMessage(senderRef, childNodeId) =>
      // TODO: test if this is correct!
      println("pool request result message")
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
        // TODO: if this is the final node
        performMetaNodeCleanUpProcedure(value)
      }
      else {
        senderRef ! ResultNotReadyMessage()
      }
    case ResultNotReadyMessage() =>
      // TODO: test if this is correct!
      ActorManager.rootActorSystem.scheduler.scheduleOnce(Config.retryInCaseResultNotReadyFrequency, sender, RequestResultMessage(self, ref.nodeId))

  }
  def start() = {
    for ((value, job) <- ref.poolCollection) {
      val valueExecutor = context.actorOf(Props(new ValueExecutor(value)), value.nodeId.toString)
      valueExecutor ! StartMessage()
    }
  }
}
