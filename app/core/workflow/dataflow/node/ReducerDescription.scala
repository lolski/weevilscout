package core.workflow.dataflow.node

import java.util.UUID
import core.types.JS_Types._
import scala.collection.mutable.ListBuffer
import scala.xml.XML
import core.exception.ParamListUninitializedException
import models.helper._
import scala.Some
import core.types.JOB_Types._
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/22/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ReducerDescription(override val userId: UUID, val wId: UUID, override val name: String, val src: String,
                         val paramId: String, override val returntype: JS = JS_String,
                         val jobtype: JOB = JOB_Javascript)
  extends SimpleNode(userId, Some(wId), name, returntype) {
  var isInitialized = false
  var length = -1
  val parameterMap: Map[String, (Option[ListBuffer[String]], JS)] = Map(paramId -> (Some(ListBuffer[String]()), JS_Array))
  def initialize(paramCount: Int) = {
    length = paramCount
    isInitialized = true
  }
  def generateXml() = {
    <weevil>
      <name>{name}</name>
      <jobtype>{jobTypeToString(jobtype)}</jobtype>
      <finalnode>{finalNode}</finalnode>
      <parameters>
        {
          for ((k, optval) <- parameterMap) yield {
            val params = optval._1 match {
              case Some(v) =>
                "<"+k+">" + "<"+k+".0>[" + v.mkString(", ") + "]</"+k+".0>" + "</"+k+">"
              case None =>
                throw new ParamListUninitializedException()
            }
            XML.loadString(params)
          }
        }
      </parameters>
      <source>{src}</source>
    </weevil>
  }
}
