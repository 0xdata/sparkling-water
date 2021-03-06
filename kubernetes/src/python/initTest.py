#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from pysparkling import *
from pyspark.sql import SparkSession
import h2o

# Start Cluster
spark = SparkSession.builder.appName("App name").getOrCreate()
hc = H2OContext.getOrCreate()
assert h2o.cluster().cloud_size == int(spark.sparkContext.getConf().get("spark.executor.instances"))

# Prepare Data
frame = h2o.import_file("https://raw.githubusercontent.com/h2oai/sparkling-water/master/examples/smalldata/prostate/prostate.csv")
sparkDF = hc.asSparkFrame(frame).repartition(10)
sparkDF = sparkDF.withColumn("CAPSULE", sparkDF.CAPSULE.cast("string"))
[trainingDF, testingDF] = sparkDF.randomSplit([0.8, 0.2])

# Train Model
from pysparkling.ml import H2OXGBoost
estimator = H2OXGBoost(labelCol = "CAPSULE")
model = estimator.fit(trainingDF)

# Run Predictions
model.transform(testingDF).collect()
hc.stop()
spark.stop()
