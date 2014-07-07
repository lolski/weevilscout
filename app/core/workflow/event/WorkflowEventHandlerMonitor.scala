package core.workflow.event

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 1/1/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.UUID
import net.minidev.json._
import core.types.Monitor_Action_Types._
import core.workflow.{WorkflowMonitorVisualization, WorkflowDescriptionVisualization, WorkflowInstance}
import core.workflow.actor._
import core.workflow.dataflow.node.{ReducerDescription, JobDescription, Node}
import core.workflow.actor.message.TerminateWorkflowSupervisorMessage
import monitor.{WorkflowMonitor, Record}

class WorkflowEventHandlerMonitor(nodeId: UUID) extends WorkflowEvents {
  val m = new WorkflowMonitor(nodeId)
  var start = 0
  var stop = 0

  override def onWorkflowStarted(inst: WorkflowInstance) {
    m.markWorkflowStarted()
    println("Workflow started")
  }

  override def onWorkflowFinished(inst: WorkflowInstance) {
    // monitor.js stop
    val elapsed = m.getElapsedTime
    m.markWofkflowFinished()

    val workflowActorPath = getWorkflowActorPathFromWorkflowId(inst.nodeId)
    val workflowActorRef = ActorManager.rootActorSystem.actorFor(workflowActorPath)
    workflowActorRef ! TerminateWorkflowSupervisorMessage()
    println("Workflow finished in " + elapsed + "ns")

    WorkflowDescriptionVisualization.generateWorkflowStructureAsGraphVisualization(inst)
    WorkflowMonitorVisualization.generateWorkflowExecutionPerformanceAsTimelineVisualization(m)
  }
  override def onExecutorStarted(node: Node) {
  }
  override def onExecutorFinished(node: Node, result: String) {
  }
  override def onJobExecutorDequeued(job: JobDescription) {
    val r = new Record(job.nodeId, m.getElapsedTime, Monitor_Action_Start, 1, "")
    m.recordAction(r)
    start += 1
  }
  override def onJobExecutorResultReceived(job: JobDescription, result: String) {
    val flops = try {
      core.result.getFlopsFromResult(result)
    }
    catch {
      case e:NumberFormatException =>
        println("incorrect result (NFE) received from reducer " + job + ": " + result + " with message " + e.getMessage())
        -1.0
      case e:Exception =>
        println("incorrect result (_) received from reducer " + job + ": " + result + " with message " + e.getMessage())
        -1.0
    }
    val r = new Record(job.nodeId, m.getElapsedTime, Monitor_Action_Stop, flops, "")
    m.recordAction(r)
    stop += 1
  }
  override def onReducerExecutorDequeued(reducer: ReducerDescription) {
    val r = new Record(reducer.nodeId, m.getElapsedTime, Monitor_Action_Start, 1, "")
    m.recordAction(r)
    start += 1
  }
  override def onReducerExecutorResultReceived(reducer: ReducerDescription, result: String) {
    val flops = try {
      core.result.getFlopsFromResult(result)
    }
    catch {
      case e:NumberFormatException =>
        println("incorrect result (NFE) received from reducer " + reducer + ": " + result + " with message " + e.getMessage())
        -1.0
      case e:Exception =>
        println("incorrect result (_) received from reducer " + reducer + ": " + result + " with message " + e.getMessage())
        -1.0
    }
    val r = new Record(reducer.nodeId, m.getElapsedTime, Monitor_Action_Stop, flops, "")
    m.recordAction(r)
    stop += 1
  }
}
