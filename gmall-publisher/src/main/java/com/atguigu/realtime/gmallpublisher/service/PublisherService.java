package com.atguigu.realtime.gmallpublisher.service;

import java.util.Map;

/**
 * Created by Liusx on 2020/8/18 15:14
 *
 * @Description:
 */
public interface PublisherService {
    Long getDau(String date);
    Map<String, Long> getHourDate(String date);
}
