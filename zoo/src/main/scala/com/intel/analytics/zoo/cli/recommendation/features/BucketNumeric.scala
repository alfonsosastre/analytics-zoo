package com.intel.analytics.zoo.cli.recommendation.features

import com.intel.analytics.zoo.models.recommendation.Utils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, udf}

case class BucketNumeric(name: String,ranges: Array[Float]) extends Feature {
  val finalName = s"${name}_bucketNum"

  val bucketUdf: UserDefinedFunction = udf(Utils.bucketizedColumn(ranges))

  override def addColumn(in: DataFrame): DataFrame = in.withColumn(finalName,bucketUdf(col(name)))

  override val dimension: Int = ranges.length
}
