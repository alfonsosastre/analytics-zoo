package com.intel.analytics.zoo.cli.recommendation.features

case class Continuous(name: String) extends Feature {
  override val finalName: String = name
  override val dimension: Int = 1
}
