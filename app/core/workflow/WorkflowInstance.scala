package core.workflow

import actor._
import java.util.UUID
import parser.WorkflowParser
import scala.collection.mutable.{Map => MMap}
import scala.xml.{Node => XmlNode}
import akka.actor.{ActorSystem => AkkaActorSystem, ActorRef}
import scala.concurrent.Await
import akka.pattern.ask
import config._
import core.types._
import core.workflow.actor.message._
import core.workflow.dataflow.node._
import core.workflow.event.WorkflowEventHandlerMonitor
import core.workflow.dataflow.node.pool.PoolDescription
import core.types.WorkflowDescription

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:36 AM
 * To change this template use File | Settings | File Templates.
 */

class WorkflowInstance(override val userId: UUID, override val name: String, val content: XmlNode)
    (implicit val actorSystem: AkkaActorSystem, val rootSupervisor: ActorRef)
    extends Node(userId, None, name) {
  val wdesc = new WorkflowDescription(WorkflowParser.parseFromXML(userId, nodeId, content), null)
  val nodeCollectionAsDiGraph = wdesc.asDiGraph
  val nodeCollectionAsHashMap: Map[UUID, Node] = wdesc.asHashMap
  val metanodeCollectionAsHashMap = wdesc.metanodeCollectionAsHashMap

  implicit val timeout = Config.actorCreationTimeout
  val workflowSupervisor =
    Await.result(rootSupervisor ? SpawnWorkflowSupervisorMessage(nodeId, nodeId.toString),
      Config.actorCreationTimeout.duration).asInstanceOf[ActorRef]

  val eventHandlersMonitor = new WorkflowEventHandlerMonitor(nodeId)
  def enqueue() = {
    val startNodeCollection = wdesc.startNodeCollection
    if (WorkflowParser.checkType(startNodeCollection.asInstanceOf[Set[Any]], nodeCollectionAsDiGraph, nodeId)) {
      for (elem <- startNodeCollection) {
        bootstrapExecution(elem)
      }
      eventHandlersMonitor.onWorkflowStarted(this)
    }
    else {
      println("ERROR: incompatible parameter and return type on some of the node. Make sure that the receiving parameter has the same matching type as the sender's return type")
    }
  }

  def bootstrapExecution[T <: WorkflowGraphDescription[Node]#NodeT](current: T) = {
    val node = current.value.asInstanceOf[Node]
    //println(node.name)
    if (node.isInstanceOf[JobDescription]) {
      // fire a new actor
      val jobNode = node.asInstanceOf[JobDescription]
      val outgoing = (for (edge <- current.outgoing) yield {
        import scalax.collection.edge.LBase.LEdgeImplicits
        object LDEdge_String extends LEdgeImplicits[String]
        import LDEdge_String._
        val destNode = edge.edge.to.value.asInstanceOf[Node]
        val destParam: String = edge
        (destNode, destParam)
      }).toList.toSet
      jobNode.outgoing = outgoing
      workflowSupervisor ! SpawnJobMessage(jobNode)
    }
    else if (node.isInstanceOf[ValueDescription]) {
      val valueNode = node.asInstanceOf[ValueDescription]
      workflowSupervisor ! SpawnValueMessage(valueNode)
    }
    else if (node.isInstanceOf[PoolDescription]) {
      // you can't because where would you get the input to the pool from?
    }
  }
  def findValueInstance(nodeId: UUID): Option[Node] = {
    nodeCollectionAsHashMap.get(nodeId) match {
      case Some(node) =>
        return Some(node)
      case None =>
        for ((metanodeId, metanode) <- metanodeCollectionAsHashMap) {
          if (metanode.isInstanceOf[PoolDescription]) {
            val poolNode = metanode.asInstanceOf[PoolDescription]
            if (poolNode.isInitialized) {
              return poolNode.findValueInstance(nodeId)
            }
          }
        }
        None
    }
  }
}
