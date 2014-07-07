package core.workflow.actor.executor

import akka.actor.Actor
import core.workflow.dataflow.node.{Node, DummyNode}
import core.workflow.actor.message._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/7/12
 * Time: 3:27 AM
 * To change this template use File | Settings | File Templates.
 */
class DummyNodeExecutor(override val ref: DummyNode) extends Actor with Executor[Node] {
  def receive = {
    case StartMessage() =>
    case _ =>
      println("dummy node executor: receiving _ message")
  }
}
