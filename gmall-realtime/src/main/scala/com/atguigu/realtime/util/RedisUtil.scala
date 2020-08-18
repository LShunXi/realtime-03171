package com.atguigu.realtime.util

import redis.clients.jedis.Jedis

/**
 * Created by Liusx on 2020/8/18 10:38
 *
 * @Description:
 */
object RedisUtil {
  val host: String = "hadoop102"
  val port: Int = 6379
  val getRedisClient: Jedis = new Jedis(host, port)
}
