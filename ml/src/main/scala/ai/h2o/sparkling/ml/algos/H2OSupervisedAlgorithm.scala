/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.h2o.sparkling.ml.algos

import ai.h2o.sparkling.ml.models.H2OSupervisedMOJOModel

import ai.h2o.sparkling.{H2OColumnType, H2OFrame}
import hex.Model
import hex.genmodel.utils.DistributionFamily
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.types.StructType

import scala.reflect.ClassTag

abstract class H2OSupervisedAlgorithm[P <: Model.Parameters: ClassTag] extends H2OAlgorithm[P] {

  def getLabelCol(): String

  def getOffsetCol(): String

  def getWeightCol(): String

  def setLabelCol(value: String): this.type

  def setOffsetCol(value: String): this.type

  def setWeightCol(value: String): this.type

  @DeveloperApi
  override def transformSchema(schema: StructType): StructType = {
    val transformedSchema = super.transformSchema(schema)
    require(
      schema.fields.exists(f => f.name.compareToIgnoreCase(getLabelCol()) == 0),
      s"Specified label column '${getLabelCol()} was not found in input dataset!")
    require(
      !getFeaturesCols().exists(n => n.compareToIgnoreCase(getLabelCol()) == 0),
      "Specified input features cannot contain the label column!")
    transformedSchema
  }

  override def fit(dataset: Dataset[_]): H2OSupervisedMOJOModel = {
    super.fit(dataset).asInstanceOf[H2OSupervisedMOJOModel]
  }

  override private[sparkling] def getExcludedCols(): Seq[String] = {
    super.getExcludedCols() ++ Seq(getLabelCol(), getWeightCol(), getOffsetCol())
      .flatMap(Option(_)) // Remove nulls
  }
}
