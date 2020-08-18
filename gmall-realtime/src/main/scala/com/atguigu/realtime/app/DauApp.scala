package com.atguigu.realtime.app

import com.alibaba.fastjson.JSON
import com.atguigu.realtime.bean.StartupLog
import com.atguigu.realtime.util.{MyKafkaUtil, RedisUtil}
import com.atguigu.util.Constant
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

/**
 * Created by Liusx on 2020/8/18 10:17
 *
 * @Description:
 */
object DauApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))

    // 1. 从kfk获取启动日志流
    val sourceStream: DStream[String] = MyKafkaUtil.getKafkaStream(ssc, Constant.STARTUP_LOG_TOPIC)
    // 2. 解析启动日志, 每条日志放入一个样例类
    val startupLogStream: DStream[StartupLog] = sourceStream.map(jsonLog => JSON.parseObject(jsonLog, classOf[StartupLog]))

    /*// 3. 去重 保留每个设备的第一次启动记录
    val filteredStartupLogStream: DStream[StartupLog] = startupLogStream.filter(log => {
      val client: Jedis = RedisUtil.getRedisClient//不支持序列化
      // 把mid写入redis的set, 返回0 保留, 返回1 去重
      // key:"mids:2020-08-18"  value: set(mid1, mid2, ...)
      val key: String = s"mids:${log.logDate}"
      val result = client.sadd(key, log.mid)
      client.close()// 因为redis连接不支持序列化
      //if(result == 1) true else false
      result == 1
    })*/

    // 3. 一个分区建立一个到redis的连接
    val filteredStartupLogStream: DStream[StartupLog] = startupLogStream.mapPartitions(startuoLogIt => {
      val client: Jedis = RedisUtil.getRedisClient //不支持序列化
      val result: Iterator[StartupLog] = startuoLogIt.filter(log => {
        val key: String = s"mids:${log.logDate}"
        val r = client.sadd(key, log.mid)
        r == 1
      })
      client.close()
      result
    })

    // 4. 把数据写入hbase by Phoenix
    // 输出算子 : print/save.../foreachRDD(常用)
    //filteredStartupLogStream.print(10000)
    import org.apache.phoenix.spark._
    filteredStartupLogStream.foreachRDD(rdd => {
      rdd.saveToPhoenix(
        "GMALL_DAU",
        Seq("MID", "UID", "APPID", "AREA", "OS", "CHANNEL", "LOGTYPE", "VERSION", "TS", "LOGDATE", "LOGHOUR"),
        zkUrl = Some("hadoop102,hadoop103,hadoop104:2181")
      )
    })
    // 启动流
    ssc.start()
    // 阻止进程退出
    ssc.awaitTermination()
  }
}


//存在问题: 与redis连接数太多(有一次启动就产生一个),会对redis造成负担
//解决方案: 一个分区只产生一个连接
/*val filteredStartupLogStream: DStream[StartupLog] = startupLogStream.filter(log => {
val client: Jedis = RedisUtil.getRedisClient
// 把mid写入redis的set, 返回0 保留, 返回1 去重
// key:"mids:2020-08-18"  value: set(mid1, mid2, ...)
val key: String = s"mids:${log.logDate}"
val result = client.sadd(key, log.mid)
client.close()
//if(result == 1) true else false
result == 1
})*/

