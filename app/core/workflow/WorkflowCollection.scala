package core.workflow

import akka.actor.{Props, DeadLetter}
import test.ListenToDeadLetters

import scala.xml.{Node => XmlNode}
import collection.mutable.{HashMap => MHashMap}
import java.util.UUID
import core.workflow.actor._
import monitor.WorkflowMonitor

object WorkflowCollection {
  var start = 0
  var stop = 0
  val globalMonitor = new WorkflowMonitor()
  implicit val rootActorSystem = ActorManager.rootActorSystem
  implicit val rootSupervisor = ActorManager.rootSupervisor
  val workflowInstances = MHashMap.empty[UUID, WorkflowInstance]
  val listenToDeadLetters = rootActorSystem.actorOf(Props(new ListenToDeadLetters()), "listenToDeadLetters")
  rootActorSystem.eventStream.subscribe(listenToDeadLetters, classOf[DeadLetter])
	def launch(userId: UUID, content: XmlNode) {
    val instance = new WorkflowInstance(userId, UUID.randomUUID().toString, content)
    instance.enqueue() // execute all the jobs in the workflow
    workflowInstances.put(instance.nodeId, instance)

	}
  def findWorkflowInstance(id: UUID) = {
    workflowInstances.get(id)
  }

}
