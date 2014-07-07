package core

import net.minidev.json._
import core.exception.ValueNotAllowedException

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/23/12
 * Time: 2:36 AM
 * To change this template use File | Settings | File Templates.
 */
package object result {
  def getFlopsFromResult(result: String) = {
    if (JSONValue.isValidJson(result)) {
      val parsed = JSONValue.parse(result)
      if (parsed.isInstanceOf[JSONArray]) {
        val parsedArray = parsed.asInstanceOf[JSONArray]
        val a = parsedArray.get(3)
        if (a.isInstanceOf[Double]) {
          a.asInstanceOf[Double]
        }
        else if (a.isInstanceOf[java.math.BigDecimal]) {
          a.asInstanceOf[java.math.BigDecimal].doubleValue()
        }
        else if (a.isInstanceOf[java.lang.Integer]) {
          a.asInstanceOf[java.lang.Integer].toDouble
        }
        else {
          throw new NumberFormatException("for value " + a + " of class " + a.getClass())
          -1.0
        }
      }
      else {
        -1.0
      }
    }
    else {
      -1.0
    }
  }
}
