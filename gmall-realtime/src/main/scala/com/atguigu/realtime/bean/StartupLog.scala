package com.atguigu.realtime.bean

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

/**
 * Created by Liusx on 2020/8/18 10:23
 *
 * @Description:
 */
case class StartupLog(mid: String,
                      uid: String,
                      appId: String,
                      area: String,
                      os: String,
                      logType: String,
                      eventId: String,
                      pageId: String,
                      nextPageId: String,
                      itemId: String,
                      ts: Long,
                      var logDate: String = null,
                      var logHour: String = null) {
  val date: Date = new Date(ts)
  logDate = new SimpleDateFormat("yyyy-MM-dd").format(date)
  logHour = new SimpleDateFormat("HH").format(date)
  // localdate功能更强大
  //  logDate = LocalDate.ofEpochDay(ts).toString

}
