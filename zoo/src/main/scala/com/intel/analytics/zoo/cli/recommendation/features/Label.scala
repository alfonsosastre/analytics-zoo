package com.intel.analytics.zoo.cli.recommendation.features
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, udf}

case class Label(name: String,classMapping: Map[String,Int]) extends Feature {

  val finalName = "label"

  private def mapLabel: UserDefinedFunction = udf((key: String) => classMapping.getOrElse(key,1))

  override def addColumn(in: DataFrame): DataFrame = in.withColumn(finalName,mapLabel(col(name)))

  override val dimension: Int = 1
}
