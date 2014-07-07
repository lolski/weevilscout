package core.workflow.actor.executor

import akka.actor.{Props, Actor}
import core.workflow.actor.message._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/6/12
 * Time: 3:46 AM
 * To change this template use File | Settings | File Templates.
 */

class RootExecutor extends Actor {
  def receive = {
    case SpawnWorkflowSupervisorMessage(workflowId, name) =>
      val workflowSupervisor = context.actorOf(Props(new WorkflowExecutor(workflowId, name)), name=name)
      workflowSupervisor ! StartMessage()
      sender ! workflowSupervisor
    case _ =>
      println("rootSupervisor: holahola")
  }
}
