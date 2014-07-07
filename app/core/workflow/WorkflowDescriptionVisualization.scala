package core.workflow

import java.util.UUID
import java.io.{FileWriter, File}
import scala.collection.Set
import scala.sys.process.Process
import core.workflow.dataflow.node._
import core.types.WorkflowGraphDescription
import core.workflow.{WorkflowInstance => CWorkflow}
import core.workflow.dataflow.node.pool.PoolDescription
import util.getWorkflowResultDirectory

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:14 AM
 * To change this template use File | Settings | File Templates.
 */

object WorkflowDescriptionVisualization {
  def generateDotFromWorkflowDescription(g: WorkflowGraphDescription[Node]) = {

    val nodes: Set[String] =
      for (v: g.NodeT <- g.nodes) yield {
        val node = v.value.asInstanceOf[Node]
        val id = "\"" + node.nodeId + "\""
        var label = "label="
        if (node.isInstanceOf[JobDescription]) {
          label += node.asInstanceOf[JobDescription].name + ", color=\"#ff6666\", style=filled"
        }
        else if (node.isInstanceOf[ValueDescription]) {
          label += node.asInstanceOf[ValueDescription].name + ", color=lightblue, style=filled"
        }
        else if (node.isInstanceOf[DummyNode]) {
          label += node.asInstanceOf[DummyNode].name + ", color=white, style=dashed"
        }
        else if (node.isInstanceOf[PoolDescription]) {
          label += node.asInstanceOf[PoolDescription].name + ", color=grey, shape=box, style=dashed"

        }
        id + "[" + label + "]\n"
      }
    val edges: Set[String] = for (e: g.EdgeT <- g.edges) yield {
      val source = e.edge.from.value.asInstanceOf[Node]
      val dest = e.edge.to.value.asInstanceOf[Node]
      val sourceId = "\"" + source.nodeId + "\""
      val destId = "\"" + dest.nodeId + "\""
      sourceId + "->" + destId + "\n"
    }
    "digraph {\n" + nodes.mkString + edges.mkString + "}"
  }
  def generatePngFromDotFile(dotFilePath: String, workflowId: UUID) = {
    val dotPath = "dot"
    val outFilePath = getWorkflowResultDirectory(workflowId) + "/workflow.png"
    val outFile = new File(outFilePath)
    val dotArg = dotFilePath + " -Tpng -o " + outFilePath
    val dotProcess = Process(dotPath + " " + dotArg)
    val status = dotProcess.run()

    outFilePath
  }
  def generateWorkflowStructureAsGraphVisualization(workflow: CWorkflow) = {
    val dotContent = generateDotFromWorkflowDescription(workflow.nodeCollectionAsDiGraph)
    val dotFileDir = new File(getWorkflowResultDirectory(workflow.nodeId))
    dotFileDir.mkdirs()
    val dotFilePath = dotFileDir.getCanonicalPath + "/workflow.dot"
    val dotFile = new File(dotFilePath)
    val dotFileWriter = new FileWriter(dotFile.getAbsoluteFile())
    dotFileWriter.write(dotContent)
    dotFileWriter.close()
    val imgPath = generatePngFromDotFile(dotFilePath, workflow.nodeId)
    imgPath
  }
}
