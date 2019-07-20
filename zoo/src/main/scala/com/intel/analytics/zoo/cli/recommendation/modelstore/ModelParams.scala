package com.intel.analytics.zoo.cli.recommendation.modelstore

import org.json4s.JsonAST.JValue
import org.json4s.jackson.Serialization
import org.json4s._

object ModelParams {

  val listOfModels = List(
    classOf[com.intel.analytics.zoo.cli.recommendation.modelstore.WideAndDeepParams]
  )

  def apply(jvalue: JValue): ModelParams = {
    implicit val formats = Serialization.formats(FullTypeHints(ModelParams.listOfModels))

    jvalue.extract[ModelParams]
  }

}

trait ModelParams
