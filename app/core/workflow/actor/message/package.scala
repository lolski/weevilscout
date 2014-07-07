package core.workflow.actor

import java.util.UUID
import akka.actor.ActorRef
import core.workflow.dataflow.node.{JobDescription, ValueDescription}

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/6/12
 * Time: 4:08 AM
 * To change this template use File | Settings | File Templates.
 */
package object message {
  sealed trait ActorMessage
  case class Hi(val msg: String) extends ActorMessage

  case class SpawnWorkflowSupervisorMessage(val workflowId: UUID, val name: String) extends ActorMessage
  case class TerminateWorkflowSupervisorMessage() extends ActorMessage
  case class RestartWorkflowSupervisorMessage() extends ActorMessage

  case class SpawnValueMessage(val ref: ValueDescription) extends ActorMessage
  case class SpawnJobMessage(val ref: JobDescription) extends ActorMessage
  case class RequestResultMessage(senderRef: ActorRef, nodeId: UUID) extends ActorMessage
  case class ResultNotReadyMessage() extends ActorMessage

  case class StartMessage() extends ActorMessage
  case class WorkerResultMessage(val value: String) extends ActorMessage
  case class KeepAliveMessage() extends ActorMessage
  case class ParamInputMessage(val key: String, val value: String) extends ActorMessage
  case class WorkerTimeoutMessage() extends ActorMessage
}
