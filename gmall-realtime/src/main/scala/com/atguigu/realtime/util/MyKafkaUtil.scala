package com.atguigu.realtime.util

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
//import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
//import org.apache.spark.streaming.kafka010.{KafkaUtils, LocationStrategies,ConsumerStrategies}

/**
 * Created by Liusx on 2020/8/18 11:04
 *
 * @Description:
 */
object MyKafkaUtil {
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "spark0317",
    // 从kfk最新位置开始消费
    "auto.offset.reset" -> "latest",
    // offset是否自己自动提交
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  def getKafkaStream(ssc: StreamingContext, topic: String) = (
    KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      Subscribe[String, String](Set(topic), kafkaParams)
    ).map(_.value())
    )
}
