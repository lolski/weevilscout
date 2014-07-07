package core.workflow.actor.executor

import scala.collection.mutable.{Map => MMap}
import java.util.UUID
import akka.actor.{ActorContext, Props, ActorRef}
import core.workflow.actor.message.{RequestResultMessage, ParamInputMessage, StartMessage}
import core.workflow.actor.executor.pool.{PoolExecutor}
import core.workflow.dataflow.node._
import core.workflow.dataflow.node.pool.{PoolDescription}
import core.workflow.WorkflowCollection
import core.exception.EntryNotFoundException

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/12/12
 * Time: 3:14 AM
 * To change this template use File | Settings | File Templates.
 */
trait Executor[T <: Node] {
  val ref: T

  def startSupervisedChildren(context: ActorContext) = {
    for (n <- ref.outgoing if (isAnAssignedSupervisorOf(n._1.nodeId))) yield {
      val destPair = n
      val destNode = destPair._1
      val destParam = destPair._2
      if (destPair._1.isInstanceOf[JobDescription]) {
        val jobNode = destNode.asInstanceOf[JobDescription]
        val executor = context.actorOf(Props(new JobExecutor(jobNode)), jobNode.nodeId.toString)
        executor ! StartMessage()
        (executor, destParam, destNode)
      }
      else if (destNode.isInstanceOf[ValueDescription]) {
        val valueNode = destNode.asInstanceOf[ValueDescription]

        val executor = context.actorOf(Props(new ValueExecutor(valueNode)), valueNode.nodeId.toString)
        executor ! StartMessage()
        (executor, destParam, destNode)
      }
      else if (destNode.isInstanceOf[PoolDescription]) {
        val poolNode = destNode.asInstanceOf[PoolDescription]
        val executor = context.actorOf(Props(new PoolExecutor(poolNode)), poolNode.nodeId.toString)
        executor ! StartMessage()
        (executor, destParam, destNode)
      }
      else if (destNode.isInstanceOf[ReducerDescription]) {
        val reducerNode = destNode.asInstanceOf[ReducerDescription]
        val executor = context.actorOf(Props(new ReducerExecutor(reducerNode)), reducerNode.nodeId.toString)
        executor ! StartMessage()
        (executor, destParam, destNode)
      }
      else /*(n.isInstanceOf[DummyNode]) */ {
        val dummyNode = destNode.asInstanceOf[DummyNode]
        val executor = context.actorOf(Props(new DummyNodeExecutor(dummyNode)), dummyNode.nodeId.toString)
        executor ! StartMessage()
        (executor, destParam, destNode)
      }
    }
  }
  def isAnAssignedSupervisorOf(nodeId: UUID) = {
    if (ref.assignedSupervisor.contains(nodeId)) {
      true
    }
    else {
      false
    }
  }
  def sendResultToSupervisedChildren(supervisedChildren: Set[(ActorRef, String, Node)], value: String) = {
    for (receiver <- supervisedChildren) {
      receiver._1 ! ParamInputMessage(receiver._2, value)
    }

  }
  def notifyNonSupervisorParent(senderRef: ActorRef, context: ActorContext): Unit = {
    for (parent <- ref.nonSupervisorParentCollection) {
      var actorPath = ""
      for (partial <- parent.actorPath) {
        actorPath += partial
      }
      actorPath = actorPath.dropRight(1)
      val actorRef = context.actorFor(actorPath)
      actorRef ! RequestResultMessage(senderRef, ref.nodeId)
    }
  }
}
