package com.hongya.bigdata.broker.audit

import com.hongya.bigdata.broker.util.{KafkaUtil, Settings}
import org.apache.spark.streaming.StreamingContext
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by dengziming on 23/10/2017.
  * ${Main}
  */
object AuditMain extends BaseMain{

  val logger: Logger = LoggerFactory.getLogger(this.getClass)


  def main(args: Array[String]): Unit = {

    /*if (args.length < 1) {
      System.err.println("Usage: Main <conf-file>")
      System.exit(1)
    }*/

    val ssc = createContext()

    streamingOperate(ssc)

    ssc.start()
    ssc.awaitTermination()

  }


  override def streamingOperate(ssc: StreamingContext): Unit ={

    val streaming = KafkaUtil.createDirectStreaming(ssc,
      Settings.get("bootstrap.servers"),
      Settings.get("group.id"),
      Settings.get("auto.offset.reset"),
      Settings.getBoolean("enable.auto.commit",default = false),
      Settings.get("kafka.order.topic").split(",")
    )


    KafkaUtil.processStreaming(streaming){ (rdd,_) =>

      val results = rdd.collect().flatMap(AuditService.audit)

      KafkaUtil.saveToKafka(results,
        Settings.get("kafka.audit.topic"),
        Settings.get("bootstrap.servers"))
    }

  }
}
