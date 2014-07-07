package core.workflow.dataflow.node

import java.util.UUID
import scala.collection.mutable.{ListBuffer, Set => MSet}
import core.types.ReturnValueDestination
import core.types.JOB_Types._
import core.types.JS_Types._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
class Node(val userId: UUID, val parentWorkflowId: Option[UUID], val name: String, val returntype: JS = JS_String) {
  val nodeId = UUID.randomUUID()
  // MUTABLE
  val actorPath = ListBuffer[String]()
  var traversed = false // TODO: if this represents a node that has been traversed using BFS traversal, how to define semantics of when a node is restarted (e.g. due to crash in the associated worker node)
  var outgoing = Set.empty[ReturnValueDestination]
  val assignedSupervisor = MSet.empty[UUID] // TODO: move this to core.workflow.annotation somehow, and change it to a set
  var nonSupervisorParentCollection = Set.empty[Node]
}
