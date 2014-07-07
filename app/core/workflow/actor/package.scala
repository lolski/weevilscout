package core.workflow

import collection.mutable.ListBuffer
import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/22/12
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
package object actor {
  def getActorPath(actorPathBuf: ListBuffer[String]) = {
    var actorPath = ""
    for (partial <- actorPathBuf) {
      actorPath += partial
    }
    actorPath = actorPath.dropRight(1)
    actorPath
  }
  def getParentWorkflowFromChildrenActorPath(actorPathBuf: ListBuffer[String]) = {
    val firstN = 4
    getActorPath(actorPathBuf.dropRight(actorPathBuf.size-firstN))
  }
  def getWorkflowActorPathFromWorkflowId(workflowId: UUID) = {
    "/user/rootSupervisor/" + workflowId.toString
  }
}
