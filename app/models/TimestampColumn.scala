package models

import java.text.SimpleDateFormat
import java.sql._
import anorm._

object TimestampColumn {
  implicit def rowToTimestamp: anorm.Column[Timestamp] = {
    Column.nonNull {
      (value, meta) =>
        val MetaDataItem(qualified, nullable, clazz) = meta
        //throw new Exception("" + value + "\n.\n" + qualified + "|" + nullable + "|" + clazz)
        value match {
          case d: Timestamp =>
            val dateFormat = "yyyy-MM-dd HH:mm:ss"
            val dateParser = new SimpleDateFormat(dateFormat)
            val dateStr = d.toString()
            val tstamp = new Timestamp(dateParser.parse(d.toString()).getTime())
            Right(tstamp)
            
          case d: Time =>
            val dateFormat = "HH:mm:ss"
            val dateParser = new SimpleDateFormat(dateFormat)
            //throw new Exception("" + value + "\n.\n" + qualified + "|" + nullable + "|" + clazz)
            val tstamp = new Timestamp(dateParser.parse(d.toString()).getTime())
            Right(tstamp)
            
          case _ =>
            Left(TypeDoesNotMatch("Can't convert value " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to java.sql.Timestamp for column " + qualified))
        }
    }
  }
}
