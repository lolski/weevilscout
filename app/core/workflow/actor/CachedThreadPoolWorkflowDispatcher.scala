package core.workflow.actor

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 10/3/12
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
object CachedThreadPoolWorkflowDispatcher {
  val tp = Executors.newCachedThreadPool()
  val ec = ExecutionContext.fromExecutorService(tp)
  def getExecutionContext = ec;
}
