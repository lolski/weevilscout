package controllers

import play.api.mvc.{Action, Controller}
import core.workflow.actor.ActorManager
import core.workflow.actor.message._
/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/19/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
object Debug extends Controller {
  def sendactormsg = Action {
    req =>
      val path = req.queryString("path")(0)

      println("sending actor a message..." + path)
      val actorRef = ActorManager.rootActorSystem.actorFor(path)
      actorRef ! ResultNotReadyMessage()
      Ok
  }
}
