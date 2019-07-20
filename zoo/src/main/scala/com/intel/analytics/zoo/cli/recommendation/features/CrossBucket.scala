package com.intel.analytics.zoo.cli.recommendation.features

import com.intel.analytics.zoo.models.recommendation.Utils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, udf}

case class CrossBucket(names: Array[String], bucketSize: Int,override val embedOutDim: Option[Int] = None)
  extends Feature {

  val finalName = s"${names.mkString("_")}_bucketString"

  private def bucketUdf: UserDefinedFunction = names.length match {
    case 2  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String))
    case 3  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String))
    case 4  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String))
    case 5  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String, _: String))
    case 6  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String, _: String, _: String))
    case 7  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String, _: String, _: String, _: String))
    case 8  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String, _: String, _: String, _: String, _: String))
    case 9  => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String, _: String, _: String, _: String, _: String, _: String))
    case 10 => udf(Utils.buckBuckets(bucketSize)(_: String, _: String, _: String, _: String, _: String, _: String, _: String, _: String, _: String, _: String))
    case _ => throw new Exception("Invalid number of columns in CrossColumn")
  }

  override def addColumn(in: DataFrame): DataFrame = in.withColumn(finalName,bucketUdf(names.map(col): _*))

  override val dimension: Int = bucketSize
}
