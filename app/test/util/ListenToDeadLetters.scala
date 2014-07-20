package test.util

import akka.actor.{Actor, DeadLetter}
import core.workflow.actor.message.RequestResultMessage

/**
 * Created by lolski on 7/8/14.
 */
class ListenToDeadLetters extends Actor {
  def receive = {
    case DeadLetter(msg, from, to) =>
      msg match {
        case RequestResultMessage(sender, nodeId) =>
          println()
          println("--- deadletter content ---")
          println("RequestResultMessage");
          println("sender" +sender.path.toString())
          println("sender nodeId: " + nodeId.toString())
          println("from: " + from.path.toString())
          println("to: " + to.path.toString())
          println("--- end ---")
          println()

        case _ =>
          println()
          println("--- deadletter content ---")
          println("--- Other type ---")
          println("--- end ---")
      }
  }

}