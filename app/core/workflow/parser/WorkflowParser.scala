package core.workflow.parser

import java.util.UUID
import xml.{Node => XmlNode}
import collection.mutable.{ListBuffer, Map => MMap}
import akka.actor.ActorSystem
import collection.Set
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.edge.LDiEdge
import scalax.collection.GraphTraversal.VisitorReturn._
import core.exception.NodeTypeNotAllowedException
import core.types._
import core.types.JS_Types._
import core.workflow.actor.CachedThreadPoolWorkflowDispatcher
import core.workflow.dataflow.node._
import core.workflow.dataflow.node.pool.PoolDescription

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/20/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
object WorkflowParser {
  def parseFromXML(userId: UUID, workflowId: UUID, content: XmlNode)(implicit actorSystem: ActorSystem) = {
    val wdesc = generateWorkflowDescription(userId, workflowId, content)
    val asDiGraph = wdesc.asDiGraph
    val startNodeCollection = wdesc.startNodeCollection
    initializeOutgoingNodeInformation(startNodeCollection, asDiGraph)
    assignSupervisorNode(asDiGraph)
    resetTraversalInfo(asDiGraph)
    precalculateActorPath[Any](startNodeCollection.asInstanceOf[Set[Any]], asDiGraph, workflowId)
    resetTraversalInfo(asDiGraph)
    asDiGraph
  }
  def resetTraversalInfo(wdesc: WorkflowGraphDescription[Node]): Unit = {
    for (n <- wdesc.nodes) {
      n.value.asInstanceOf[Node].traversed = false
    }
  }
  def checkType[T](startNodeCollection: Set[T], wdesc: WorkflowGraphDescription[Node], workflowId: UUID): Boolean = {
    for (edge <- wdesc.edges) {
      val from = edge.edge.from.value.asInstanceOf[Node]

      import scalax.collection.edge.LBase.LEdgeImplicits
      object LDEdge_String extends LEdgeImplicits[String]
      val destNode = edge.edge.to.value.asInstanceOf[Node]
      val destParam: String = LDEdge_String.innerEdge2UserLabel(edge)

      if (destNode.isInstanceOf[JobDescription]) {
        val jobNode = destNode.asInstanceOf[JobDescription]
        jobNode.parameterMap match {
          case Some(param) =>
            val paramtype = param(destParam)._2
            if (from.returntype != paramtype) {
              return false
            }
          case None =>
        }
      }
      else if (destNode.isInstanceOf[PoolDescription]) {
        val poolNode = destNode.isInstanceOf[PoolDescription]
        if (from.returntype != JS_Array) {
          return false
        }
      }
    }
    true
  }
  def printtype(wdesc: WorkflowGraphDescription[Node]) = {
    for (n <- wdesc.nodes) {
      val node = n.value.asInstanceOf[Node]
      println("node " + node.name + ": " + node.returntype)
      if (n.value.isInstanceOf[JobDescription]) {
        val job = n.value.asInstanceOf[JobDescription]
        job.parameterMap match {
          case Some(param) =>
            for ((pk, pv) <- param) {
              println("   param " + pk + " " + pv)
            }
          case None =>

        }
      }
    }
  }

  private def generateWorkflowDescription(userId: UUID, workflowId: UUID, xml: XmlNode)(implicit actorSystem: ActorSystem) = {
    implicit val ec = CachedThreadPoolWorkflowDispatcher.getExecutionContext
    val verticesXmlNode = xml \\ "workflow" \ "nodes" \ "_"
    val verticeMap = MMap[String, Node]()
    val vertices =
      for (elem <- verticesXmlNode) yield {
        if (elem.label == "pool") {
          val metanode = NodeParser.parsePool(userId, workflowId, elem)
          verticeMap += (metanode.name -> metanode)
          metanode
        }
        else if (elem.label == "job") {
          val jobNode = NodeParser.parseJob(userId, workflowId, elem)
          verticeMap += (jobNode.name -> jobNode)
          jobNode
        }
        else if (elem.label == "value") {
          val valueNode = NodeParser.parseValue(userId, workflowId, elem)
          verticeMap += (valueNode.name -> valueNode)
          valueNode
        }
        else if (elem.label == "reducer") {
          val reducerNode = NodeParser.parseReducer(userId, workflowId, elem)
          verticeMap += (reducerNode.name -> reducerNode)
          reducerNode
        }
        else {
          //val node = NodeParser.parseNode(userId, workflowId, elem)
          //verticeMap += (node.name -> node) // TODO: change the attribute "name" to "id" in the XML?
          //node
          throw new NodeTypeNotAllowedException()
        }
    }

    // process all edges
    val edgesXmlNode = xml \\ "workflow" \ "edges" \ "_"
    val edgeList =
      for (elem <- edgesXmlNode) yield {
        val id = (elem \ "@id").text
        val source = (elem \ "@source").text
        val dest = (elem \ "@dest").text
        val paramDest = (elem \ "@paramdest").text

        LDiEdge(verticeMap(source), verticeMap(dest))(paramDest)
      }

    val finalnodeId = (xml \\ "workflow" \ "finalnode" \ "@id").text
    // build graph`
    val workflowAsDiGraph: WorkflowGraphDescription[Node] = Graph.from(vertices, edgeList)
    val finalNodeAsGraphNode = workflowAsDiGraph.get(verticeMap(finalnodeId))

    // TODO: simplify this as both Simple and Meta node have finalNode field (move it to Node class?)
    if (verticeMap(finalnodeId).isInstanceOf[SimpleNode]) { // TODO: remove this
      val finalNode = verticeMap(finalnodeId).asInstanceOf[SimpleNode]
      finalNode.finalNode = true

    }
    else if (verticeMap(finalnodeId).isInstanceOf[MetaNode]) { // TODO: remove this
      val finalNode = verticeMap(finalnodeId).asInstanceOf[MetaNode]
      finalNode.finalNode = true

    }

    new WorkflowDescription(workflowAsDiGraph, finalNodeAsGraphNode)
  }
  private def initializeOutgoingNodeInformation[T <: WorkflowGraphDescription[Node]#NodeT](startNodeCollection: Set[T], wdesc: WorkflowGraphDescription[Node]): Unit = {
    val startNodeCollectionUnboxed = startNodeCollection.asInstanceOf[Set[wdesc.NodeT]]
    def visit(current: wdesc.NodeT) = {
      val currentNode = current.value.asInstanceOf[Node]
      if (!currentNode.traversed) {
        currentNode.traversed = true
        currentNode.outgoing =
          (for (edge <- current.outgoing) yield {
            // WARNING:
            // if some time the edge conversion failed to work, put Graph[Node, LDiEdge]#EdgeT and Graph[Node, LDiEdge]#NodeT and Graph[Node, LDiEdge] for edge, current, and wdesc respectively
            //
            import scalax.collection.edge.LBase.LEdgeImplicits
            object LDEdge_String extends LEdgeImplicits[String]

            val destNode = edge.edge.to.value.asInstanceOf[Node]
            val destParam: String = LDEdge_String.innerEdge2UserLabel(edge)
            (destNode, destParam)
          }).toList.toSet
        Continue
      }
      else {
        Cancel
      }
    }
    val bfs = wdesc.newTraversal(nodeVisitor = visit)
    for (elem <- startNodeCollectionUnboxed) {
      bfs(elem, breadthFirst = true)
    }
  }
  private def assignSupervisorNode(wdesc: WorkflowGraphDescription[Node]): Unit = {
    // if a certain node has more than one incoming edges E
    for (node <- wdesc.nodes if wdesc.nodes.size > 0) {
      if (node.diPredecessors.size > 1) {
        val assignedSupervisor = node.diPredecessors.head.value.asInstanceOf[Node]
        val pred =
          for (elem <- node.diPredecessors) yield {
            elem.value.asInstanceOf[Node]
          }
        val rest = pred - assignedSupervisor
        assignedSupervisor.value.asInstanceOf[Node].assignedSupervisor += node.value.asInstanceOf[Node].nodeId
        // non-supervisor parent info so the child can contact them when they get alive
        node.value.asInstanceOf[Node].nonSupervisorParentCollection =
          (for (elem <- rest) yield {
            elem
          }).toSet
      }
      else {
        node.diPredecessors.headOption match {
          case Some(x) =>
            x.value.asInstanceOf[Node].assignedSupervisor += node.value.asInstanceOf[Node].nodeId
          case None =>
        }
      }
    }
  }
  private def precalculateActorPath[T](startNodeCollection: Set[T], wdesc: WorkflowGraphDescription[Node], workflowId: UUID): Unit = {
    // this implementation really messes with your head, so i'll have a few words
    // first the algorithm starts with appending the rootActorPath to the starting nodes
    // so now the nodes contain an incomplete path: the path to its predecessor actor
    // then in each node n (i.e. the method visit(n):
    //  finalize of the path by appending the node id
    //  for the outgoing of each node n
    //    do the same as above, fill in the incomplete path only to be fullfilled later in the call in visit(n)
    def visit(current: wdesc.NodeT): Unit = {
      val currentNode = current.value.asInstanceOf[Node]
      if (!currentNode.traversed) {
        currentNode.traversed = true
        currentNode.actorPath += (currentNode.nodeId.toString + "/")
        for (next <- current.diSuccessors) {
          val nextNode = next.value.asInstanceOf[Node]
          if (currentNode.assignedSupervisor.contains(nextNode.nodeId)) {
            nextNode.actorPath ++= currentNode.actorPath.clone()
            visit(next)
          }
        }
      }
    }

    val startNodeCollectionUnboxed = startNodeCollection.asInstanceOf[Set[wdesc.NodeT]]
    val rootActorPath = ListBuffer[String]("/", "user/", "rootSupervisor/", (workflowId.toString + "/"))
    for (n <- startNodeCollectionUnboxed) {
      val node = n.value.asInstanceOf[Node]
      node.actorPath ++= rootActorPath.clone()
      visit(n)
    }
  }
}
