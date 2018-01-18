package com.hongya.bigdata.broker.audit

import com.hongya.bigdata.broker.util.Settings
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by dengziming on 05/12/2017.
  * ${Main}
  */
class BaseMain {

  @transient private var instance: SparkSession = _

  def createContext(): StreamingContext ={
    //初始化
    println("初始化Streaming...")
    // 初始化spark, 设置spark参数
    val conf = new SparkConf().setAppName(Settings.get("app.name","app"))

    if (Settings.get("env") == "dev") {
      conf.setMaster("local[2]")
      conf.set("spark.testing.memory", "2048000000")
      conf.set("spark.streaming.kafka.maxRatePerPartition","100")
    }

    if (!conf.contains("spark.streaming.kafka.maxRatePerPartition")) {
      conf.set("spark.streaming.kafka.maxRatePerPartition", s"""${Settings.getInt("spark.streaming.kafka.maxRatePerPartition")}""")
    }

    val sc = new SparkContext(conf)
    new StreamingContext(sc, Seconds(Settings.getInt("spark.app.window.second")))
  }

  def streamingOperate(ssc: StreamingContext): Unit ={

  }


  def getSession(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      val enableHiveSupport = Settings.getBoolean("enableHiveSupport",false)
      val builder = SparkSession.builder
      if (enableHiveSupport){
        instance = builder.enableHiveSupport()
          .config(sparkConf)
          .getOrCreate()
      }else{
        instance = builder
          .config(sparkConf)
          .getOrCreate()
      }
    }
    instance
  }
}
