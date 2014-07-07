package core.workflow.event

import core.workflow.dataflow.node._
import core.workflow.WorkflowInstance

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 1/1/13
 * Time: 1:35 AM
 * To change this template use File | Settings | File Templates.
 */
trait WorkflowEvents {
  def onWorkflowStarted(inst: WorkflowInstance): Unit
  def onWorkflowFinished(inst: WorkflowInstance): Unit
  def onExecutorStarted(inst: Node): Unit
  def onJobExecutorDequeued(inst: JobDescription): Unit
  def onJobExecutorResultReceived(job: JobDescription, result: String)
  def onReducerExecutorDequeued(inst: ReducerDescription): Unit
  def onReducerExecutorResultReceived(job: ReducerDescription, result: String)
  def onExecutorFinished(inst: Node, result: String): Unit
}
