package com.hongya.bigdata.broker.audit

import com.alibaba.fastjson.{JSON, JSONObject}
import com.hongya.bigdata.broker.util.Env

import scala.collection.mutable.ArrayBuffer

/**
  * Created by hongya on 08/01/2018.
  * ${Main}
  */
object AuditService {

  val start: Long = Env.time

  def main(args: Array[String]): Unit = {

    println(evaluate(8,0))
    println(evaluate(8,0))
    println(evaluate(8,0))
    println(evaluate(1,0))

    audit("""{
            |"userid":523963,"date":"2018-01-11 11:40:53","orderid":"20180111114053129"
            |}
            |""".stripMargin).foreach(println)
  }
  /**
    * 模拟人工信审和机器信审
    */
  def audit(line:String): Seq[String] ={

    println(line)
    val result = ArrayBuffer[String]()
    val input = JSON.parseObject(line)
    val orderid = input.getString("orderid")
    val userid = input.getString("userid")
    val date = input.getString("date")


    var rule001 = randomValue()
    var rule002 = randomValue()
    var rule003 = randomValue()
    var rule004 = randomValue()
    var rule005 = randomValue()
    var rule006 = randomValue()
    var rule007 = randomValue()
    var rule008 = randomValue()
    var rule009 = randomValue()
    var rule010 = randomValue()


    val json = new JSONObject()

    json.put("orderid",orderid)
    json.put("userid",userid)
    json.put("date",date)

    json.put("code","人工")
    json.put("rule001",rule001)
    json.put("rule002",rule002)
    json.put("rule003",rule003)
    json.put("rule004",rule004)
    json.put("rule005",rule005)
    json.put("rule006",rule006)
    json.put("rule007",rule007)
    json.put("rule008",rule008)
    json.put("rule009",rule009)
    json.put("rule010",rule010)

    result += json.toJSONString


    rule001 = evaluate(probability(start,System.currentTimeMillis()),rule001)
    rule002 = evaluate(probability(start,System.currentTimeMillis()),rule002)
    rule003 = evaluate(probability(start,System.currentTimeMillis()),rule003)
    rule004 = evaluate(probability(start,System.currentTimeMillis()),rule004)
    rule005 = evaluate(probability(start,System.currentTimeMillis()),rule005)
    rule006 = evaluate(probability(start,System.currentTimeMillis()),rule006)
    rule007 = evaluate(probability(start,System.currentTimeMillis()),rule007)
    rule008 = evaluate(probability(start,System.currentTimeMillis()),rule008)
    rule009 = evaluate(probability(start,System.currentTimeMillis()),rule009)
    rule010 = evaluate(probability(start,System.currentTimeMillis()),rule010)


    json.put("orderid",orderid)
    json.put("userid",userid)
    json.put("date",date)

    json.put("code","机器")
    json.put("rule001",rule001)
    json.put("rule002",rule002)
    json.put("rule003",rule003)
    json.put("rule004",rule004)
    json.put("rule005",rule005)
    json.put("rule006",rule006)
    json.put("rule007",rule007)
    json.put("rule008",rule008)
    json.put("rule009",rule009)
    json.put("rule010",rule010)

    result += json.toJSONString

    result
  }


  def evaluate(probability:Int,value:Int): Int ={

    if ((Math.random() * 10) < probability){
      value
    }else{
      1 - value
    }
  }

  def probability(start:Long,time:Long): Int ={

    val value = ((time - start) / (1000 * 30)).toInt


    if (value > 9) 9 else value

  }

  def randomValue(): Int ={

    if (Math.random() < 0.5) 0 else 1
  }
}
