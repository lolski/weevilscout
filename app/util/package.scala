import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:04 AM
 * To change this template use File | Settings | File Templates.
 */
package object util {
  def getJobFullPath(id: String) = {
    val root = "public/job"
    root + "/" + id + "/" + id + ".js"
  }
  def getWorkflowResultDirectory(workflowId: UUID) = {
    val root = "public/workflow_results/"
    root + workflowId.toString()
  }
  def getInputDirectory(file: String) = {
    val root = "public/inputs/"
    root + file
  }

  def getWorkflowProfileDirectory(workflowId: UUID) = {
    val root = "public/workflow_results/"
    root + workflowId.toString()
  }
  def getWorkflowProfileDirectoryAsset(workflowId: UUID) = {
    val root = "assets/workflow_results/"
    root + workflowId.toString()
  }

}
