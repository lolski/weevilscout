package core.workflow.actor

import akka.actor.{Actor, ActorSystem , Props}
import executor.RootExecutor

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/5/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
object ActorManager {
  val rootActorSystem = ActorSystem("actorSystem")
  val rootSupervisor = rootActorSystem.actorOf(Props[RootExecutor], name="rootSupervisor")
}
