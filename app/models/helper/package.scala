package models

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/5/12
 * Time: 3:02 AM
 * To change this template use File | Settings | File Templates.
 */

import core.types.JOB_Types._
import core.exception.{InvalidMarkupDescriptionException, ValueNotAllowedException}

package object helper {
  def jobTypeStringToColumn(jobtype: String) = {
    if (jobtype == "javascript") {
      0
    }
    else if (jobtype == "webcl") {
      // webcl
      1
    }
    else {
      throw new ValueNotAllowedException("jobtype column must contain either 0 (javascript) or 1 (webcl)")
    }
  }
  def jobTypeToString(jobtype: JOB) = {
    if (jobtype == JOB_Javascript) {
      "javascript"
    }
    else if (jobtype == JOB_WebCL) {
      "webcl"
    }
    else {
      throw new InvalidMarkupDescriptionException("jobtype must be either JOB_Javascript or JOB_WebCL. Found: " + jobtype)
    }
  }
}
