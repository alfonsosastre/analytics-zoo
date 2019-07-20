package com.intel.analytics.zoo.cli.recommendation.features

import com.intel.analytics.zoo.models.recommendation.Utils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{udf,col}

case class Categorical(name: String,vocabulary: Array[String],override val embedOutDim: Option[Int]=None) extends Feature {

  val finalName = s"${name}_cat"

  val dimension: Int = vocabulary.length

  private def vocabularyUdf = udf(Utils.categoricalFromVocabList(vocabulary))

  override def addColumn(in: DataFrame): DataFrame = in.withColumn(finalName,vocabularyUdf(col(name)))

}
