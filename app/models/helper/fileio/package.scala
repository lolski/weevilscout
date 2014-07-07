package models.helper

import java.io._
import java.util.UUID
import core.exception.ValueNotAllowedException

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/5/12
 * Time: 2:55 AM
 * To change this template use File | Settings | File Templates.
 */
package object fileio {
  def writeJobResult(jobId: String, jobName: String, result: String) = {
    val dir = new File("public/results/" + jobName)
    dir.mkdirs
    val path = "public/results/" + jobName + "/" + jobId + "_OUT.html"
    val file = new File(path)
    val writer = new FileWriter(file.getAbsoluteFile())
    writer.write(result)
    writer.close()
    path
  }
  def writeWorkflowResult(workflowId: String, jobId: String, result: String) = {
    val workflowPath = "public/workflow_results/" + workflowId
    val dir = new File(workflowPath)
    dir.mkdirs()
    val path = workflowPath + "/" + jobId + "_OUT.html"
    val file = new File(path)
    file.createNewFile()
    val writer = new FileWriter(file.getAbsoluteFile())
    writer.write(result)
    writer.close()
    path
  }
  def writeJobInstance(workflowId: UUID, jobId: UUID, jobDescription: scala.xml.Node) {
    val name = (jobDescription \ "name")(0).text
    val jobtype = (jobDescription \ "jobtype")(0).text
    val source = (jobDescription \ "source")(0).text
    val isAFinalNode =
      if ((jobDescription \ "finalnode")(0).text == "true") {
        1
      }
      else {
        0
      }
    val newSource =
      if (jobtype == "javascript") {
        var parameters = ""
        (jobDescription \ "parameters" \ "_").foreach(
          n =>
            parameters += ("var " + n.label + " = e.data." + n.label + ";")
        )
        SourceCodeTransformer.transformJSCode(source, parameters)
      }
      else if (jobtype == "webcl") {
        val parameters = (jobDescription \ "parameters" \ "_")
        val paramHashMap = (for (param <- parameters) yield {
          val content = (param \ "_").text
          val key = param.label
          (key, content) // TODO: (key, content, type)???
        }).toMap
        SourceCodeTransformer.transformWebCLCode(source, paramHashMap)
      }
      else {
        throw new ValueNotAllowedException("jobtype must be either webcl or javascript")
      }

    try {
      val pathStr = util.getWorkflowProfileDirectory(workflowId) + "/"
      val path = new File(pathStr)
      path.mkdirs()
      val job_xml_file = new FileWriter(pathStr + jobId.toString + ".xml")
      val job_xml_data =
        <weevil-job>
          {
          scala.xml.XML.loadString(
            workflowId match {
              case null => // TODO: remove
                "<noworkflow></noworkflow>"
              case uuid: UUID =>
                "<workflow-id>" + workflowId.toString + "</workflow-id>"
            }
          )
          }
          <job-id>{jobId.toString}</job-id>
          <name>{name}</name>
          <jobtype>{jobtype}</jobtype>
          <parameters>{jobDescription \ "parameters" \ "_"}</parameters>
          <source>{util.getWorkflowProfileDirectoryAsset(workflowId) + "/" + name + ".js"}</source>
        </weevil-job>
      job_xml_file.write(job_xml_data.toString)
      job_xml_file.close()
      // insert job script
      val new_source_file = new File(pathStr + name + ".js")
      new_source_file.createNewFile
      val new_source_writer = new PrintWriter(new_source_file.getAbsoluteFile)
      val newSourceBufWriter = new BufferedWriter(new_source_writer)
      newSourceBufWriter.write(newSource)
      newSourceBufWriter.newLine()
      newSourceBufWriter.close()
    }
    catch {
      case fnfe: FileNotFoundException =>
    }
  }

}
