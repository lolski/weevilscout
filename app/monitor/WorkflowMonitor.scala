package monitor

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, Map => MMap, Set => MSet}
import core.types.Monitor_Action_Types._
import core.types.RecordCollection

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/16/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */

class WorkflowMonitor(var workflowId: UUID = null) {
  var workflowStart: Long = 0
  var workflowFinish: Long = 0
  var recordAsSet = MSet[(Long, Int)]()
  var recordAsList = ArrayBuffer[(Long, RecordCollection)]()
  var startActionCollection = MMap[UUID, Record]()

  def markWorkflowStarted() {
    workflowStart = System.nanoTime()
  }
  def markWofkflowFinished() {
    workflowFinish = System.nanoTime()
  }
  def getElapsedTime = {
    System.nanoTime() - workflowStart
  }
  def recordAction(rec: Record) {
    val elem = recordAsSet.find(time => rec.time == time._1)
    elem match {
      case None =>
        val collection = MSet(rec)
        val positionInList = recordAsList.size - 1
        recordAsList += ((rec.time, collection))
        recordAsSet += ((rec.time, positionInList))
      case Some(x) =>
        val positionInList = x._2
        val recCollection = recordAsList(positionInList)
        recCollection._2 += rec
    }
    if (rec.action == Monitor_Action_Stop) {
      startActionCollection(rec.nodeId).FLOPS = rec.FLOPS
    }
    else if (rec.action == Monitor_Action_Start) {
      startActionCollection(rec.nodeId) = rec
    }
  }
}
