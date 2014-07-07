package core.workflow.dataflow.node.pool

import java.util.UUID
import scala.collection.mutable.{Map => MMap, Set => MSet}
import scala.xml.{Node => XmlNode}
import core.types.JS_Types._
import core.types.ParamList
import core.workflow.dataflow.node._
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/19/12
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */

class PoolDescription(override val userId: UUID, val wId: UUID, override val name: String,
                      val parameterMap: ParamList, val processorPrototype: JobDescription,
                      val reducer: ReducerDescription, override val returntype: JS = JS_String)
  extends MetaNode(userId, Some(wId), name) {
  var isInitialized = false
  var length = 0

  val poolCollection = MSet[(ValueDescription, JobDescription)]()

  def initialize(value: String) {
    val arrayValue = value.drop(1).dropRight(1).split(",")
    var i = 0
    for (elem <- arrayValue) {
      val in = new ValueDescription(userId, parentWorkflowId, name + ":processor_input:" + i, elem)

      val src = processorPrototype.src
      val paramsAsHashMap =
        MMap[String, (Option[String], JS)](
          ((for ((key, (value, type_)) <- processorPrototype.parameterMap.get) yield {
            (key , (value, type_))
          }).toMap).toSeq: _*
        )
      val proc = new JobDescription(userId, parentWorkflowId, name + ":processor_job:" + i, src, Some(paramsAsHashMap), processorPrototype.returntype, processorPrototype.jobtype)
      poolCollection += ((in, proc))
      i+= 1
    }

    length = arrayValue.size
    initializeOutgoingNodeInformation()
    precalculateActorPath()
    assignSupervisorNode()
    reducer.initialize(length)
    isInitialized = true
  }
  def findValueInstance(nodeId: UUID): Option[Node] = {
    for ((_, job) <- poolCollection) {
      if (nodeId == job.nodeId) {
        return Some(job)
      }
    }
    if (reducer.nodeId == nodeId) {
      return Some(reducer)
    }
    return None
  }
  private def initializeOutgoingNodeInformation(): Unit = {
    for ((in, proc) <- poolCollection) {
      // in ~> (proc, procParam)
      val procParam = proc.parameterMap.get.keys.head
      in.outgoing = Set((proc, procParam))

      // proc ~> (reducer, reducerParam)
      val reducerParam = reducer.parameterMap.keys.head
      proc.outgoing = Set((reducer, reducerParam))

      // reducer ~> (this.outgoing)
      reducer.outgoing = outgoing
    }
  }
  private def precalculateActorPath(): Unit = {
    val root = actorPath
    for ((value, job) <- poolCollection) {
      value.actorPath ++= root.clone()
      value.actorPath += value.nodeId.toString + "/"
      job.actorPath ++= value.actorPath.clone()
      job.actorPath += job.nodeId.toString + "/"
    }
    val reducerSupervisor = poolCollection.head._2
    reducer.actorPath ++= reducerSupervisor.actorPath.clone()
    reducer.actorPath += reducer.nodeId + "/"
  }
  private def assignSupervisorNode(): Unit = {
    this.assignedSupervisor ++=
      (for ((value, _) <- poolCollection) yield {
        value.nodeId
      })
    for ((value, job) <- poolCollection) {
      value.assignedSupervisor += job.nodeId
    }
    val firstProcessor = poolCollection.head._2
    firstProcessor.assignedSupervisor += reducer.nodeId

    val rest = (poolCollection - poolCollection.head).map(pair => pair._2)
    reducer.nonSupervisorParentCollection = rest.toSet

    reducer.assignedSupervisor ++= assignedSupervisor
  }
}
