package com.intel.analytics.zoo.cli.recommendation.modelstore

case class WideAndDeepParams(modelType: String = "wide_n_deep",
                             inputDir: String = "./data/ml-1m/",
                             batchSize: Int = 2048,
                             maxEpoch: Int = 10,
                             logDir: Option[String] = None,
                             numClasses: Int = 2,
                             hiddenLayers: Array[Int] = Array(100,40,20)
                  ) extends ModelParams
