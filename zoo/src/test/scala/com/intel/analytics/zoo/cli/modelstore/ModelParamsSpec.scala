package com.intel.analytics.zoo.cli.modelstore

import com.intel.analytics.zoo.cli.recommendation.modelstore.{ModelParams, WideAndDeepParams}
import org.scalatest.{FlatSpec, Matchers}
import org.json4s.jackson.JsonMethods._
import org.json4s._

class ModelParamsSpec extends FlatSpec with Matchers {

   val resource = scala.io.Source.fromFile("/Users/alfonso.sastre/IdeaProjects/analytics-zoo/zoo/src/test/resources/cli/census.json").getLines().mkString("")
   val jconf = parse(resource)

  "ModelParams" should "be able to read from a JValue object" in {

    println(pretty(jconf \ "modelParams"))

    val res = ModelParams(jconf \ "modelParams")
    val expected = WideAndDeepParams("wide_n_deep","/input",2048,10,Some("/tmp/census-log"),2,Array(100,75,50,25))

    expected should be equals(res)

  }

}
