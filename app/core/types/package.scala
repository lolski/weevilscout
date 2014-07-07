package core

import java.util.UUID
import scala.collection.mutable.{Map => MMap, Set => MSet}
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import core.workflow.dataflow.node.{MetaNode, Node}
import monitor.Record

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:22 AM
 * To change this template use File | Settings | File Templates.
 */

package object types {
  object JOB_Types extends Enumeration {
    type JOB = Value
    val JOB_Javascript, JOB_WebCL, JOB_Invalid = Value
    def fromString(str: String) = {
      if (str == "javascript") {
        JOB_Javascript
      }
      else if (str == "webcl") {
        JOB_WebCL
      }
      else {
        JOB_Invalid
      }
    }
  }
  object JS_Types extends Enumeration {
    type JS = Value
    val JS_Number, JS_String, JS_Boolean, JS_Array, JS_Object, JS_Invalid = Value
    def fromString(str: String) = {
      if (str == "String") {
        JS_String
      }
      else if (str == "Number") {
        JS_Number
      }
      else if (str == "Boolean") {
        JS_Boolean
      }
      else if (str == "Array") {
        JS_Array
      }
      else if (str == "Object") {
        JS_Object
      }
      else {
        JS_Invalid
      }
    }

  }
  object Monitor_Action_Types extends Enumeration {
    type Monitor_Action = Value
    val Monitor_Action_Start, Monitor_Action_Stop, Monitor_Action_Intermediate = Value
  }
  type RecordCollection = MSet[Record]

  type ReturnValueDestination = (Node, String)
  type WorkflowGraphDescription[T] = Graph[T, LDiEdge]
  type ParamList = Option[MMap[String, (Option[String], JS_Types.JS)]]

  class WorkflowDescription(val asDiGraph: WorkflowGraphDescription[Node], val finalNode_ : Any ) {
    val startNodeCollection = for (e <- asDiGraph.nodes if e.diPredecessors.size == 0) yield e
    val finalNode = finalNode_.asInstanceOf[asDiGraph.NodeT]
    val asHashMap =
      (for (elem <- asDiGraph.nodes) yield {
        val elemNode = elem.value.asInstanceOf[Node]
        (elemNode.nodeId, elemNode)
      }).toMap

    val metanodeCollectionAsHashMap = MMap.empty[UUID, Node]
      for ((k, v) <- asHashMap) {
        if (v.isInstanceOf[MetaNode]) {
          metanodeCollectionAsHashMap(k) = v
        }
      }
  }
}
