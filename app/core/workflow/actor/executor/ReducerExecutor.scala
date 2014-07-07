package core.workflow.actor.executor

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/22/12
 * Time: 3:52 AM
 * To change this template use File | Settings | File Templates.
 */
import akka.actor.{Actor}
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import config.Config
import core.workflow.actor.message._
import core.workflow.actor.{ActorManager, CachedThreadPoolWorkflowDispatcher}
import models.JobQueueDB
import core.workflow.dataflow.node.ReducerDescription
import core.workflow.WorkflowCollection

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/5/12
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 */
class ReducerExecutor(override val ref: ReducerDescription)
  extends Actor with Executor[ReducerDescription] with SimpleExecutorCleanUpProcedure[ReducerDescription] {
  implicit val ec = CachedThreadPoolWorkflowDispatcher.getExecutionContext
  val value = Promise[String]

  override def receive = {
    case StartMessage() =>
      markThisNodeAsStarted()
      notifyNonSupervisorParent(self, context)

      // TODO: are these code needed? when is a reducer gets automatically populated right after it starts? probably in case reducer happen to receive no parameter?
      if (isParameterMapPopulated()) {
        JobQueueDB.enqueueJob(ref.userId, ref.wId, ref.nodeId, ref.generateXml())
      }

    case ParamInputMessage(key, value) =>
      tryAssignParamValue(key, value)
      if (isParameterMapPopulated()) {
        JobQueueDB.enqueueJob(ref.userId, ref.wId, ref.nodeId, ref.generateXml())
      }

    case WorkerResultMessage(value) =>
      this.value.success(value)

      // get path to pool executor which is a remote parent of this instance of reducer executor
      // TODO: move this code inside pool: pool should supply the outgoing address
      val poolExecutorActorPathBuf = ref.actorPath.dropRight(3)
      val poolActorPath = core.workflow.actor.getActorPath(poolExecutorActorPathBuf)
      //pass the result to pool
      val poolActorRef = ActorManager.rootActorSystem.actorFor(poolActorPath)
      poolActorRef ! WorkerResultMessage(value)
      performSimpleNodeCleanUpProcedure(value) // TODO: check! e.g. is there a case where this node terminates before the message to pool even arrive?

    case RequestResultMessage(senderRef, childNodeId) =>
      // TODO: is this even called?
      println("WARNING: request result message in reducer executor")
      var key: Option[String] = None
      for (pair <- ref.outgoing) {
        if (pair._1.nodeId == childNodeId) {
          key = Some(pair._2)
        }
      }
      if (value.isCompleted) {
        val value = Await.result(this.value.future, 1 seconds)
        senderRef ! ParamInputMessage(key.get, value)
      }
      else {
        senderRef ! ResultNotReadyMessage() // TODO
      }
    case ResultNotReadyMessage() =>
      ActorManager.rootActorSystem.scheduler.scheduleOnce(Config.retryInCaseResultNotReadyFrequency, sender, RequestResultMessage(self, ref.nodeId))

    case KeepAliveMessage() =>

    case WorkerTimeoutMessage() =>
  }

  private def tryAssignParamValue(key: String, value: String) = {
    ref.parameterMap.head._2._1.get += value // append value into ListBuffer[String]
    true
  }
  private def isParameterMapPopulated() = {
    // TODO
    val paramCollection = ref.parameterMap.head._2._1.get
    if (ref.length == paramCollection.size) {
      true
    }
    else {
      false
    }
  }
}

