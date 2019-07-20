package com.intel.analytics.zoo.cli.recommendation.features

import com.intel.analytics.zoo.models.recommendation.ColumnFeatureInfo
import org.apache.spark.sql.DataFrame
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.jackson.Serialization


object FeatureInfo{

  def apply(jvalue: JValue): FeatureInfo = {
     implicit val formats = Serialization.formats(FullTypeHints(Feature.listOfFeatures))

     jvalue.extract[FeatureInfo]
  }

}

case class FeatureInfo(
    wideBaseCols: Option[Array[_ <: Feature]],
    wideCrossCols: Option[Array[_ <: Feature]],
    indicatorCols: Option[Array[_ <: Feature]],
    embedCols: Option[Array[_ <: Feature]],
    continuousCols: Option[Array[_ <: Feature]],
    label: Label) {

  lazy val columnFeatureInfo: ColumnFeatureInfo = ColumnFeatureInfo(
    wideBaseCols.map( _.map(_.finalName)).getOrElse(Array[String]()),
    wideBaseCols.map( _.map(_.dimension)).getOrElse(Array[Int]()),
    wideCrossCols.map( _.map(_.finalName)).getOrElse(Array[String]()),
    wideCrossCols.map( _.map(_.dimension)).getOrElse(Array[Int]()),
    indicatorCols.map( _.map(_.finalName)).getOrElse(Array[String]()),
    indicatorCols.map( _.map(_.dimension)).getOrElse(Array[Int]()),
    embedCols = embedCols.map( _.map(_.finalName)).getOrElse(Array[String]()),
    embedInDims = embedCols.map( _.map(_.dimension)).getOrElse(Array[Int]()),
    embedOutDims = embedCols.map( _.map(_.embedOutDim.get)).getOrElse(Array[Int]()),
    continuousCols = continuousCols.map(_.map(_.finalName)).getOrElse(Array[String]()),
    label.finalName
  )

  private lazy val transformations: Array[_ <: Feature] = wideCrossCols.getOrElse(Array()) ++
    wideBaseCols.getOrElse(Array()) ++ indicatorCols.getOrElse(Array()) ++ embedCols.getOrElse(Array()) ++ Array(label)

  def addColumns(in: DataFrame): DataFrame =
    transformations.foldLeft(in){ (df,tr) =>
      println(tr)
      tr.addColumn(df)
    }

}
