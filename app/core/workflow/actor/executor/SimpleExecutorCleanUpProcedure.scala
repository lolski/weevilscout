package core.workflow.actor.executor

import java.util.UUID
import scala.collection.mutable.{Map => MMap}
import core.exception.EntryNotFoundException
import core.workflow.dataflow.node.SimpleNode
import core.workflow.WorkflowCollection

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/20/12
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
trait SimpleExecutorCleanUpProcedure[T <: SimpleNode] {
  val ref: T
  val areResultsSent = MMap[UUID, Int]()
  for ((node, _) <- ref.outgoing) {
    areResultsSent += (node.nodeId -> 0)
  }
  def hasSentToAllOutgoingNode(): Boolean = {
    for ((id, status) <- areResultsSent) {
      if (status == 0) return false
    }
    return true
  }
  def markThisNodeAsStarted() {
    val workflow = WorkflowCollection.findWorkflowInstance(ref.parentWorkflowId.get)
    workflow match {
      case Some(instance) =>
        instance.eventHandlersMonitor.onExecutorStarted(ref)
      case None =>
        throw new EntryNotFoundException("Error when notifying final node finished: can't find entry for such node")
    }
  }
  def markThisNodeAsFinished(result: String) {
    val workflow = WorkflowCollection.findWorkflowInstance(ref.parentWorkflowId.get)
    workflow match {
      case Some(instance) =>
        instance.eventHandlersMonitor.onExecutorFinished(ref, result)
      case None =>
        throw new EntryNotFoundException("Error when notifying final node finished: can't find entry for such node")
    }
  }
  def markParentWorkflowAsFinished() {
    val workflow = WorkflowCollection.findWorkflowInstance(ref.parentWorkflowId.get)
    workflow match {
      case Some(instance) =>
        instance.eventHandlersMonitor.onWorkflowFinished(instance)
      case None =>
        throw new EntryNotFoundException("Error when notifying final node finished: can't find entry for such node")
    }
  }
  def performSimpleNodeCleanUpProcedure(result: String) {
    // cleanup procedure
    if (hasSentToAllOutgoingNode()) {
      markThisNodeAsFinished(result)
    }
    if (ref.finalNode) {
      markParentWorkflowAsFinished()
    }
  }
}
