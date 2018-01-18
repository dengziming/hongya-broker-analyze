package com.hongya.bigdata.broker.util

import java.util.Properties


import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{StreamingContext, Time}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by hongya on 23/10/2017.
  */
object KafkaUtil {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  var producer :KafkaProducer[Any,Any] = _

  def createDirectStreaming(ssc: StreamingContext,
                            bootstrap:String,
                            group_id:String,
                            auto_reset:String,
                            auto_commit:java.lang.Boolean = false,
                            topics:Array[String]
                           ): InputDStream[ConsumerRecord[String, String]] = {

    val props = Map[String, Object](
      "bootstrap.servers" -> bootstrap,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> group_id,
      "auto.offset.reset" -> auto_reset,
      "enable.auto.commit" -> auto_commit)

    props.foreach(prop => logger.info(s"${prop._1} -> ${prop._2}"))

    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, props))
  }

  def getProducer(bootstrap:String) : KafkaProducer[Any,Any]= {
    if (producer == null){
      val props = new Properties()
      props.put("bootstrap.servers", bootstrap)
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      producer = new KafkaProducer[Any, Any](props)
    }
    producer
  }


  def saveToKafka(lines:Seq[String],
                  topic:String,
                  bootstrap:String
                 ): Unit = {

      lines.foreach { line =>
        val producer = getProducer(bootstrap).asInstanceOf[KafkaProducer[String, String]]
        val key = System.currentTimeMillis() + ""
        val record = new ProducerRecord[String,String](topic,key,line)
        producer.send(record);

      }

  }

  def processStreaming(stream:InputDStream[ConsumerRecord[String, String]])(processBatchRddFunc: (RDD[String],Time) => Unit): Unit = {

    stream.foreachRDD((rdd, time) => {
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      //如果kafka对应某个partition的fromOffset与untilOffset一致代表没有数据，跳过不处理
      var allPartitionEmpty = true
      offsetRanges.foreach {
        range =>
          //log纪录batch的partition/offset位置
          logger.info(s"[INFO] Rdd id:${rdd.id}: offsetRanges: ${range.partition}: ${range.fromOffset}~${range.untilOffset}")
          if (range.untilOffset - range.fromOffset > 0) {
            allPartitionEmpty = false
          }
      }

      if (allPartitionEmpty) {
        logger.info("[INFO] no data from kafka, batch ignore.")
      }
      val valueRdd = rdd.map(record => {
        record.value
      })
      processBatchRddFunc(valueRdd,time)

      //最后提交offset到kafka的zk
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    })

  }
}
