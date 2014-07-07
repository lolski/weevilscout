package core.workflow.dataflow.node

import scala.xml.XML
import java.util.UUID
import core.types._
import core.types.JOB_Types._
import core.types.JS_Types._
import core.exception._
import models.helper._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:09 AM
 * To change this template use File | Settings | File Templates.
 */

class JobDescription(override val userId: UUID, override val parentWorkflowId: Option[UUID], override val name: String,
                     val src: String, val parameterMap: ParamList, override val returntype: JS = JS_String,
                     val jobtype: JOB = JOB_Javascript)
  extends SimpleNode(userId, parentWorkflowId, name, returntype) {
  def generateXml() = {
    <weevil>
      <name>{name}</name>
      <jobtype>{jobTypeToString(jobtype)}</jobtype>
      <finalnode>{finalNode}</finalnode>
      <parameters>
      {
        parameterMap match {
          case Some(x) =>
            for ((k, optval) <- x) yield {
              val params = optval._1 match {
                case Some(v) =>
                  "<"+k+">" + "<"+k+".0>" + v + "</"+k+".0>" + "</"+k+">"
                case None =>
                  throw new ParamListUninitializedException()
              }
              XML.loadString(params)
            }
          case None =>
        }
      }
      </parameters>
      <source>{src}</source>
    </weevil>
  }
}
