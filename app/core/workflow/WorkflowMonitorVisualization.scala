package core.workflow

import java.io.{FileWriter, File}
import scala.collection.mutable.ArrayBuffer
import scala.sys.process.Process
import core.types.Monitor_Action_Types._
import util._
import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/20/12
 * Time: 4:16 AM
 * To change this template use File | Settings | File Templates.
 */

object WorkflowMonitorVisualization {
  var TAB = "\t\t\t"
  var ENDL = "\r\n"
  def generateWorkflowExecutionPerformanceAsTimelineVisualization(wmon: monitor.WorkflowMonitor) = {
    val fileDir = new File(getWorkflowProfileDirectory(wmon.workflowId))
    fileDir.mkdirs()

    val dataFilePath = fileDir.getCanonicalPath + "/timeline.data"
    val dataFile = new File(dataFilePath)
    val dataWriter = new FileWriter(dataFile.getAbsoluteFile())
    dataWriter.write("#time(ns)" + TAB + "time(s)" + TAB + "MFLOPS" + ENDL)
    val timeline = accumulateFLOPSForTimelineVisualization(wmon)
    for(elem <- timeline) {
      val timeSecond = (elem._1 / 10e9)
      dataWriter.write(elem._1 + TAB + timeSecond + TAB + elem._2 + ENDL)
    }
    dataWriter.close()

    // TODO: call gnuplot
    val plotFilePath = fileDir.getCanonicalPath + "/timeline.plot"
    val plotFile = new File(plotFilePath)
    val plotWriter = new FileWriter(plotFile.getAbsoluteFile())
    plotWriter.write(generatePlotScript(wmon.workflowId))
    plotWriter.close()
    val gnuplotPath = "gnuplot"
    val gnuplotArg = getWorkflowProfileDirectory(wmon.workflowId) + "/timeline.plot"
    val gnuplotProcess = Process(gnuplotPath + " " + gnuplotArg)
    gnuplotProcess.run()
  }

  private def accumulateFLOPSForTimelineVisualization(wmon: monitor.WorkflowMonitor) = {
    val accumulation = ArrayBuffer[(Long, Double)]()
    var level = 0.0
    var startCount = 0
    var stopCount = 0
    for (record <- wmon.recordAsList) {
      for (elem <- record._2) {
        //println("." + elem.FLOPS)
        // in this if else, order of incrementing level and adding element to accumulation list is important!
        if (elem.action == Monitor_Action_Start) {
          accumulation += ((elem.time, level)) // extra points to make visual clear
          level += elem.FLOPS
          startCount += 1
          accumulation += ((elem.time, level))
        }
        else if (elem.action == Monitor_Action_Stop) {
          accumulation += ((elem.time, level))
          level -= elem.FLOPS
          stopCount += 1
          accumulation += ((elem.time, level)) // some extra points to make the visualization clear
        }
      }
    }
    println("level=" + level)
    accumulation.toList
  }

  private def generatePlotScript(id: UUID): String = {
    val outFilePath = getWorkflowProfileDirectory(id) + "/timeline.png";

    """
      |#!/usr/bin/gnuplot
      |
      |reset
      |set term png size 1024, 768
      |set ylabel "MFLOP/s"
      |set xlabel "time (sec)"
      |#set xrange [1:20]
      |set title "Achieved FLOPS Over Time"
      |set grid
      |#set logscale
      |#set yrange [1:175]
      |set format x "%1.1f"
      |set out '""".stripMargin +
        outFilePath +
      """'
      |plot """".stripMargin + getWorkflowProfileDirectory(id) + "/timeline.data" + """" using 2:3 with line title "MFLOPS"
      """.stripMargin
  }
}
