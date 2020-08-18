package com.atguigu.realtime.gmallpublisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Liusx on 2020/8/18 15:14
 *
 * @Description:
 */
public interface DauMapper {
    Long getDau(String date);

    List<Map<String, Object>> getHourDau(String date);
}
