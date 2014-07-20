package core.workflow.actor.executor

import akka.actor.Actor
import core.workflow.actor.message._
import core.workflow.dataflow.node.ValueDescription
import core.workflow.WorkflowCollection
import core.exception.EntryNotFoundException
import test.JobNameFromActorPathMap

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/7/12
 * Time: 2:24 AM
 * To change this template use File | Settings | File Templates.
 */
class ValueExecutor(override val ref: ValueDescription)
  extends Actor with Executor[ValueDescription] with SimpleExecutorCleanUpProcedure[ValueDescription] {
  override def preStart() = {
    println("Starting ValueExecutor " + ref.name)
    JobNameFromActorPathMap.collection += (self.path.toString -> ref.name)
  }

  def receive = {
    case StartMessage() =>
      markThisNodeAsStarted()
      notifyNonSupervisorParent(self, context)
      val supervisedChildren = startSupervisedChildren(context)
      sendResultToSupervisedChildren(supervisedChildren, ref.getValue())

      for ((ref, key, node) <- supervisedChildren) {
        areResultsSent(node.nodeId) = 1
      }
      performSimpleNodeCleanUpProcedure(ref.getValue())

    case RequestResultMessage(senderRef, childNodeId) =>
      var key: Option[String] = None
      for (pair <- ref.outgoing) {
        if (pair._1.nodeId == childNodeId) {
          key = Some(pair._2)
        }
      }
      senderRef ! ParamInputMessage(key.get, ref.getValue())
      areResultsSent(childNodeId) = 1
      performSimpleNodeCleanUpProcedure(ref.getValue())
  }
}
