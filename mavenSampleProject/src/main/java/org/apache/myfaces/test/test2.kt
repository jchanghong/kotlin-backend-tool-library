package org.apache.myfaces.test

import cn.hutool.poi.excel.ExcelUtil

/**
 * zdr人脸相似度对比出处理
 * SELECT i.rxsfyqid ,i.zdr_id,i.snap_pic_url,h.face_pic_url,i.similarity,i.alarm_time FROM fusion_alarm_info i ,fusion_alarm_human h
WHERE i.alarm_id=h.alarm_id
and i.zdr_id=h.zdr_id
and h.face_pic_url is not null
and i.alarm_id>'2021-12-01 00:00:01'
limit 15000
create at 2021-12-2021/12/23-19:12
@author jiangchanghong
 */
fun main() {

}
