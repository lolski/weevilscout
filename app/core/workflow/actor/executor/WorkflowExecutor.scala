package core.workflow.actor.executor

import akka.actor.{Actor, Props}
import java.util.UUID
import core.workflow.actor.ActorManager
import core.workflow.actor.message._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/6/12
 * Time: 3:52 AM
 * To change this template use File | Settings | File Templates.
 */

class WorkflowExecutor(val workflowId: UUID, val name: String) extends Actor {
  // TODO: store references to child actors??
  def receive = {
    case StartMessage() =>
    case SpawnJobMessage(ref) =>
      val executorRef = context.actorOf(Props(new JobExecutor(ref)), ref.nodeId.toString)
      executorRef ! StartMessage()
    case SpawnValueMessage(ref) =>
      val executorRef = context.actorOf(Props(new ValueExecutor(ref)), ref.nodeId.toString)
      executorRef ! StartMessage()
    case TerminateWorkflowSupervisorMessage() =>
      // TODO
      // - undo all changes such as database entries?
      ActorManager.rootActorSystem.stop(context.self)

    case RestartWorkflowSupervisorMessage() =>
    // TODO
    // - undo all changes as when database is terminated?
    // - then spawn new workflow supervisor (use new id or the same id?)
  }
}
