package com.intel.analytics.zoo.cli.recommendation

import com.intel.analytics.bigdl.dataset.{Sample, SampleToMiniBatch}
import com.intel.analytics.bigdl.nn.ClassNLLCriterion
import com.intel.analytics.bigdl.optim.{Adagrad, Ftrl, Loss, Top1Accuracy, Trigger}
import com.intel.analytics.bigdl.utils.{RandomGenerator,T}
import com.intel.analytics.zoo.cli.recommendation.features.FeatureInfo
import com.intel.analytics.zoo.cli.recommendation.modelstore.{ModelParams, WideAndDeepParams}
import com.intel.analytics.zoo.common.NNContext
import com.intel.analytics.zoo.feature.FeatureSet
import com.intel.analytics.zoo.models.recommendation.{Utils, WideAndDeep => ZooWideAndDeep}
import com.intel.analytics.zoo.pipeline.estimator.Estimator
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}

import scala.reflect.ClassTag
import org.json4s._
import org.json4s.jackson.JsonMethods._

case class WideAndDeep(conf: String) {

  private val jconf = {
    val file = scala.io.Source.fromFile(conf)
    val string = file.getLines.mkString("")
    file.close
    parse(string)
  }

  case class RecordSample[T: ClassTag](sample: Sample[T])

  def train(spark: SparkSession, trainDf: DataFrame, valDf: DataFrame): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val params = ModelParams(jconf \ "modelParams") match {
      case wndParams: WideAndDeepParams => wndParams
      case _ => throw new Exception("Unknown training parameter model.")
    }

    println(params)

    val featureInfo = FeatureInfo(jconf \ "features")

    val batchSize: Int = params.batchSize
    val maxEpoch: Int = params.maxEpoch
    val modelType: String = params.modelType

    val conf = spark.sparkContext.getConf
    val sc = NNContext.initNNContext(conf)
    val sqlContext = spark.sqlContext

    println(trainDf.show(10))

    val localColumnInfo = featureInfo.columnFeatureInfo

    println(featureInfo)

    RandomGenerator.RNG.setSeed(1)
    val wideAndDeep = ZooWideAndDeep.sequential[Float](
      params.modelType,
      numClasses = params.numClasses,
      columnInfo = localColumnInfo,
      hiddenLayers = params.hiddenLayers)

    val isImplicit = false
    val trainpairFeatureRdds =
      assemblyFeature(isImplicit, trainDf, featureInfo, params.modelType)

    val sample1 = trainpairFeatureRdds.take(10)

    val validationpairFeatureRdds =
      assemblyFeature(isImplicit, valDf, featureInfo, params.modelType)

    val optimMethods = if (modelType == "wide_n_deep") {
      Map("deepPart" -> new Adagrad[Float](0.001),
        "widePart" -> new Ftrl[Float](math.min(5e-3, 1 / math.sqrt(3049))))
    } else if (modelType == "wide") {
      Map("widePart" -> new Ftrl[Float](math.min(5e-3, 1 / math.sqrt(3049))))
    } else if (modelType == "deep") {
      Map("deepPart" -> new Adagrad[Float](0.001))
    } else {
      throw new IllegalArgumentException(s"Unkown modelType $modelType")
    }

    val sample2batch = SampleToMiniBatch[Float](batchSize)

    val trainRdds = FeatureSet.rdd(trainpairFeatureRdds.map(x => x.sample).cache()) ->
      sample2batch
    val validationRdds = FeatureSet.rdd(validationpairFeatureRdds.map(x => x.sample).cache()) ->
      sample2batch

    val estimator = if (params.logDir.isDefined) {
      val logdir = params.logDir.get
      val appName = "/census_wnd"
      Estimator[Float](wideAndDeep, optimMethods, modelDir = logdir + appName)
    } else {
      Estimator[Float](wideAndDeep, optimMethods)
    }

    val (checkpointTrigger, testTrigger, endTrigger) =
      (Trigger.everyEpoch, Trigger.everyEpoch, Trigger.maxEpoch(maxEpoch))

    estimator.train(trainRdds, ClassNLLCriterion[Float](),
      Some(endTrigger),
      Some(checkpointTrigger),
      validationRdds,
      Array(new Top1Accuracy[Float],
        new Loss[Float]()))
  }

  def loadData(sqlContext: SQLContext, format: String, options: Map[String,String], trainPath: String,validationPath: String): (DataFrame, DataFrame) = {
    val training = sqlContext.read.format(format).options(options).load(trainPath)
    val validation = sqlContext.read.format(format).options(options).load(validationPath)
    (training, validation)
  }

  // convert features to RDD[Sample[Float]]
  def assemblyFeature(isImplicit: Boolean = false,
                      dataDf: DataFrame,
                      featureInfo: FeatureInfo,
                      modelType: String): RDD[RecordSample[Float]] = {

    val data = featureInfo.addColumns(dataDf)

    data.show
    data.printSchema()

    val rddOfSample = data.rdd.map(r => {
      RecordSample(Utils.row2SampleSequential(r, featureInfo.columnFeatureInfo, modelType))
    })
    rddOfSample
  }

}
