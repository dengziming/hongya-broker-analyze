package com.hongya.bigdata.broker.audit

import java.util.Properties

import com.hongya.bigdata.broker.util.{KafkaUtil, Settings}
import org.apache.spark.sql.SaveMode
import org.apache.spark.streaming.StreamingContext
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by dengziming on 23/10/2017.
  * ${Main}
  */
object AnalyzeMain extends BaseMain{

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
      Settings.get("kafka.audit.topic").split(",")
    )

    val sql =
      """
        |
        |select
        |    userid,
        |    orderid,
        |    date,
        |    code,
        |    rule001,
        |    rule002,
        |    rule003,
        |    rule004,
        |    rule005,
        |    rule006,
        |    rule007,
        |    rule008,
        |    rule009,
        |    rule010
        |from
        |    src
      """.stripMargin

    KafkaUtil.processStreaming(streaming){ (rdd,_) =>

      val spark = getSession(rdd.sparkContext.getConf)

      if (rdd.count() < 1 ){

      }else{

        val df = spark.read.json(rdd)
        df.createOrReplaceTempView("src")
        val prop = new Properties()
        prop.put("driver", Settings.get("jdbc.driver"))
        spark.sql(sql).write.mode(SaveMode.Append).jdbc(
          Settings.get("jdbc.url"),
          Settings.get("jdbc.table"),
          prop
        )
      }

    }

  }

}
