package core.workflow.parser

import java.util.UUID
import scala.collection.mutable.{Map => MMap}
import scala.io.{Source => ioSource}
import scala.xml.{Node => XmlNode}
import core.types._
import core.types.JOB_Types._
import core.types.JS_Types._
import core.workflow.dataflow.node.{ReducerDescription, JobDescription, ValueDescription}
import core.workflow.dataflow.node.pool.{PoolDescription}
import core.exception.InvalidMarkupDescriptionException
import util._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/22/12
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
object NodeParser {
  def parseValue(userId: UUID, workflowId: UUID, elem: XmlNode) = {
    val id = (elem \ "@id")
    val returntype =
      if ((elem \ "@returntype").size > 0) {
        JS_Types.fromString((elem \\ "@returntype").text.trim)
      }
      else {
        JS_String
      }
    val value =
      if ((elem \ "@value").size == 1) {
        (elem \ "@value").text
      }
      else {
        val path = getInputDirectory((elem \ "@src").text)
        ioSource.fromFile(path).mkString.trim
      }
    val node = new ValueDescription(userId, Some(workflowId), id.text, value, returntype)
    node
  }

  def parseJob(userId: UUID, workflowId: UUID, elem: XmlNode) = {
    val id = (elem \ "@id")
    val jobtype =
      if ((elem \ "@jobtype").size > 0) {
        JOB_Types.fromString((elem \\ "@jobtype").text.trim)
      }
      else {
        JOB_Javascript
      }
    val returntype =
      if ((elem \ "@returntype").size > 0) {
        JS_Types.fromString((elem \\ "@returntype").text.trim)
      }
      else {
        JS_String
      }
    val params = (elem \ "parameters" \ "_")
    val paramsAsHashMap =
      MMap[String, (Option[String], JS)](
        ((for (param <- params) yield {
          val id = (param \ "@id")
          val paramreturntype =
            if ((param \ "@paramtype").size > 0) {
              JS_Types.fromString((param \ "@paramtype").text.trim)
            }
            else {
              JS_String
            }
          (id.text, (None, paramreturntype))
        }) toMap).toSeq: _*
      )
    val fullpath = getJobFullPath((elem \ "@src").text)
    val src = ioSource.fromFile(fullpath).mkString
    val node = new JobDescription(userId, Some(workflowId), id.text, src, Some(paramsAsHashMap), returntype, jobtype)
    node
  }

  def parseReducer(userId: UUID, workflowId: UUID, elem: XmlNode) = {
    val id = elem \ "@id"
    val jobtype =
      if ((elem \ "@jobtype").size > 0) {
        JOB_Types.fromString((elem \\ "@jobtype").text.trim)
      }
      else {
        JOB_Javascript
      }
    val parameter = elem \ "parameters" \ "_"
    val fullpath = getJobFullPath((elem \ "@src").text)
    val src = ioSource.fromFile(fullpath).mkString
    val returntype =
      if ((elem \ "@returntype").size > 0) {
        JS_Types.fromString((elem \\ "@returntype").text.trim)
      }
      else {
        JS_String
      }
    if (parameter.size != 1) {
      throw new InvalidMarkupDescriptionException("reducer node must have exactly one parameter")
    }
    else {
      val paramId = (parameter.head \ "@id").text
      val node = new ReducerDescription(userId, workflowId, id.text, src, paramId, returntype, jobtype)
      node
    }
  }
  def parsePool(userId: UUID, workflowId: UUID, elem: XmlNode) = {
    val id = elem \ "@id"
    val parameter = elem \ "parameters" \ "_"
    val returntype = elem \ "@returntype"
    val processor = elem \ "processor"
    val reducer = elem \ "reducer"

    if (parameter.size != 1) {
      throw new InvalidMarkupDescriptionException("ERROR: a pool can only have one incoming parameter")
    }
    val paramid = (parameter \ "@id").text
    val paramtype =
      if ((parameter \ "@paramtype").size == 1) {
        JS_Types.fromString((parameter \ "@paramtype").text.trim)
      }
      else {
        JS_String
      }
    if (paramtype != JS_Array) {
      throw new InvalidMarkupDescriptionException("a pool must have an Array as its sole incoming parameter")
    }

    val returntype2 =
      if ((returntype.size == 1)) {
        JS_Types.fromString(returntype.text)
      }
      else {
        JS_String
      }
    val parameterMap: ParamList = Some(MMap(paramid -> (None, paramtype)))
    val processorNode = parseJob(userId, workflowId, processor.head)
    val reducerNode = parseReducer(userId, workflowId, reducer.head)

    new PoolDescription(userId, workflowId, id.text, parameterMap,
      processorNode, reducerNode, returntype2)
  }
}
