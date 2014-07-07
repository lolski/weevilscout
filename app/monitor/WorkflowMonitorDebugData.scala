package monitor

import java.util.UUID
import core.types.Monitor_Action_Types._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/23/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
object WorkflowMonitorDebugData {
  def insertSimpleDebugData(m: WorkflowMonitor) {
    m.recordAsList.clear()
    m.recordAsSet.clear()
    val r0id = UUID.randomUUID()
    val r1id = UUID.randomUUID()
    val r0start = new Record(r0id, 0, Monitor_Action_Start, 1, "")
    val r1start = new Record(r1id, 1, Monitor_Action_Start, 1, "")
    val r1stop = new Record(r1id, 2, Monitor_Action_Stop, 1, "")
    val r0stop = new Record(r0id, 3, Monitor_Action_Stop, 1, "")

    m.recordAction(r0start)
    m.recordAction(r1start)
    m.recordAction(r1stop)
    m.recordAction(r0stop)
  }
  def insertOverlappingDebugData(m: WorkflowMonitor) {
    m.recordAsList.clear()
    m.recordAsSet.clear()

    val r0id = UUID.randomUUID()
    val r1id = UUID.randomUUID()
    val r2id = UUID.randomUUID()
    val r0start = new Record(r0id, 0, Monitor_Action_Start, 10, "")
    val r1start = new Record(r1id, 1, Monitor_Action_Start, 1, "")
    val r1stop = new Record(r1id, 2, Monitor_Action_Stop, 1, "")
    val r2start = new Record(r2id, 3, Monitor_Action_Start, 1, "")
    val r0stop = new Record(r0id, 4, Monitor_Action_Stop, 10, "")
    val r2stop = new Record(r2id, 5, Monitor_Action_Stop, 1, "")
    m.recordAction(r0start)
    m.recordAction(r1start)
    m.recordAction(r1stop)
    m.recordAction(r2start)
    m.recordAction(r0stop)
    m.recordAction(r2stop)
  }
  def insertNonAdjacentDebugData(m: WorkflowMonitor) {
    m.recordAsList.clear()
    m.recordAsSet.clear()
    val r0id = UUID.randomUUID()
    val r1id = UUID.randomUUID()
    val r0start = new Record(r0id, 0, Monitor_Action_Start, 1, "")
    val r1start = new Record(r1id, 1, Monitor_Action_Start, 1, "")
    val r1stop = new Record(r1id, 2, Monitor_Action_Stop, 1, "")
    val r0stop = new Record(r0id, 3, Monitor_Action_Stop, 1, "")

    val r2start = new Record(r0id, 8, Monitor_Action_Start, 1, "")
    val r3start = new Record(r1id, 9, Monitor_Action_Start, 1, "")
    val r3stop = new Record(r1id, 10, Monitor_Action_Stop, 1, "")
    val r2stop = new Record(r0id, 11, Monitor_Action_Stop, 1, "")

    m.recordAction(r0start)
    m.recordAction(r1start)
    m.recordAction(r1stop)
    m.recordAction(r0stop)
    m.recordAction(r2start)
    m.recordAction(r3start)
    m.recordAction(r3stop)
    m.recordAction(r2stop)
  }
}
