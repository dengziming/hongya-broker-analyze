package com.hongya.bigdata.broker.order

import java.text.SimpleDateFormat

import com.hongya.bigdata.broker.util.{KafkaUtil, Settings}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by hongya on 08/01/2018.
  */
object OrderService {

  def main(args: Array[String]): Unit = {

    orderService()
  }


  // 不断发送订单数据
  def orderService(): Unit ={

    val start = System.currentTimeMillis()

    while (true){

      val lines = ArrayBuffer[String]()
      for (_ <- Range(0,5)){
        val millis = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis())
        val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())
        val userid = (Math.random() * 1000000).toInt
        Thread.sleep(1)
        lines += (
          s"""
            |{
            |"userid":$userid,"date":"$date","orderid":"$millis"
            |}
          """.stripMargin)
      }
      lines.foreach(println)
      KafkaUtil.saveToKafka(
        lines.toSeq,
        Settings.get("kafka.order.topic"),
        Settings.get("bootstrap.servers"))
      Thread.sleep(10000)
    }

  }

}
