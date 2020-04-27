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

package org.apache.spark.h2o;

import ai.h2o.sparkling.backend.converters.SupportedRDD$;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Option;
import water.Key;
import water.fvec.Frame;
import water.fvec.H2OFrame;

/**
 * A Java-friendly version of [[org.apache.spark.h2o.H2OContext]]
 *
 * <p>Sparkling Water can run in two modes. External cluster mode and internal cluster mode. When
 * using external cluster mode, it tries to connect to existing H2O cluster using the provided spark
 * configuration properties. In the case of internal cluster mode,it creates H2O cluster living in
 * Spark - that means that each Spark executor will have one h2o instance running in it. This mode
 * is not recommended for big clusters and clusters where Spark executors are not stable.
 *
 * <p>Cluster mode can be set using the spark configuration property spark.ext.h2o.mode which can be
 * set in script starting sparkling-water or can be set in H2O configuration class H2OConf
 */
public class JavaH2OContext {
  /*
  Note for developers: This class is not written in scala intentionally as we want to have static method getOrCreate on
  the class itself and not on class generated from the object ( like H2OContext$). This way the functionality and API
  remains the same as in H2OContext, but we need to write a few pass-through functions.

  If we write this class in scala the Java users would have to call getOrCreate method on generated class ending with $
  which is not nice.
  */
  private transient H2OContext hc;

  public H2OContext h2oContext() {
    return hc;
  }

  public SparkContext sparkContext() {
    return hc.sparkContext();
  }

  /**
   * Create new JavaH2OContext based on existing H2O Context
   *
   * @param hc H2O Context
   */
  private JavaH2OContext(H2OContext hc) {
    this.hc = hc;
  }

  /**
   * Get Java H2O Context based on existing H2O Context
   *
   * @param hc H2O Context
   * @return Java H2O Context
   */
  public static JavaH2OContext getFromExisting(H2OContext hc) {
    return new JavaH2OContext(hc);
  }

  /**
   * Pass-through to H2OContext.asH2OFrame. For API support only.
   *
   * @param df data frame to pass for building an H2OFrame
   * @return a new H2O frame
   */
  public H2OFrame asH2OFrame(Dataset<Row> df) {
    return hc.asH2OFrame(df);
  }

  /**
   * Pass-through to H2OContext.asH2OFrame. For API support only.
   *
   * @param df data frame to pass for building an H2OFrame
   * @param frameName name of the new frame
   * @return a new H2O frame
   */
  public H2OFrame asH2OFrame(Dataset<Row> df, String frameName) {
    return hc.asH2OFrame(df, frameName);
  }

  /**
   * Create a new H2OFrame based on existing Frame referenced by its key.
   *
   * @param s the key
   * @return a new H2O frame
   */
  public H2OFrame asH2OFrame(String s) {
    return hc.asH2OFrame(s);
  }

  /**
   * Create a new H2OFrame based on existing Frame
   *
   * @param fr the frame to be used
   * @return Java H2O Context
   */
  public H2OFrame asH2OFrame(Frame fr) {
    return hc.asH2OFrame(fr);
  }

  /**
   * Convert given H2O frame into a RDD
   *
   * @param fr the frame to be used
   * @return a new RDD
   */
  public JavaRDD<Row> asRDD(H2OFrame fr) {
    return hc.asSparkFrame(fr, true).toJavaRDD();
  }

  @Deprecated
  public Dataset<Row> asDataFrame(Frame fr) {
    return asSparkFrame(fr);
  }

  /**
   * Convert given H2O frame into DataFrame type
   *
   * @param fr the frame to be used
   * @return a new data frame
   */
  public Dataset<Row> asSparkFrame(Frame fr) {
    return asSparkFrame(fr, true);
  }

  @Deprecated
  public Dataset<Row> asDataFrame(Frame fr, boolean copyMetadata) {
    return asSparkFrame(fr, copyMetadata);
  }

  /**
   * Convert given H2O frame into DataFrame type
   *
   * @param fr H2O frame to convert
   * @param copyMetadata true if metadata should be copied
   * @return Spark dataset
   */
  public Dataset<Row> asSparkFrame(Frame fr, boolean copyMetadata) {
    return hc.asSparkFrame(fr, copyMetadata);
  }

  @Deprecated
  public Dataset<Row> asDataFrame(String key) {
    return asSparkFrame(key);
  }

  /**
   * Convert given H2O frame into DataFrame type
   *
   * @param key key of H2O frame to convert
   * @return Spark dataset
   */
  public Dataset<Row> asSparkFrame(String key) {
    return asSparkFrame(key, true);
  }

  @Deprecated
  public Dataset<Row> asDataFrame(String key, boolean copyMetadata) {
    return asSparkFrame(key, copyMetadata);
  }

  /**
   * Convert given H2O frame into DataFrame type
   *
   * @param key key of H2O frame to convert
   * @param copyMetadata true if metadata should be copied
   * @return Spark dataset
   */
  public Dataset<Row> asSparkFrame(String key, boolean copyMetadata) {
    return hc.asSparkFrame(key, copyMetadata);
  }

  /**
   * Pass-through to H2OContext.toH2OFrameKey. For API support only
   *
   * @param df dataset to convert
   * @return key of H2O frame
   */
  public Key<Frame> toH2OFrameKey(Dataset<Row> df) {
    return hc.toH2OFrameKey(df);
  }

  /**
   * Pass-through to H2OContext.toH2OFrameKey. For API support only
   *
   * @param df dataset to convert
   * @param frameName option containing name of the frame
   * @return key of H2O frame
   */
  public Key<Frame> toH2OFrameKey(Dataset<Row> df, Option<String> frameName) {
    return hc.toH2OFrameKey(df, frameName);
  }

  /**
   * Pass-through to H2OContext.toH2OFrameKey. For API support only
   *
   * @param df dataset to convert
   * @param frameName name of the frame
   * @return key of H2O frame
   */
  public Key<Frame> toH2OFrameKey(Dataset<Row> df, String frameName) {
    return hc.toH2OFrameKey(df, frameName);
  }

  /**
   * Pass-through to H2OContext.toH2OFrameKey. For API support only
   *
   * @param df dataset to convert
   * @return String representation of H2OFrame key
   */
  public String asH2OFrameKeyString(Dataset<Row> df) {
    return hc.asH2OFrameKeyString(df);
  }

  /**
   * Pass-through to H2OContext.toH2OFrameKey. For API support only
   *
   * @param df dataset to convert
   * @param frameName name of the frame
   * @return String representation of H2OFrame key
   */
  public String asH2OFrameKeyString(Dataset<Row> df, String frameName) {
    return hc.asH2OFrameKeyString(df, Option.apply(frameName));
  }

  /**
   * Get existing or create new H2OContext
   *
   * @return H2O Context
   */
  public static JavaH2OContext getOrCreate() {
    return new JavaH2OContext(H2OContext.getOrCreate());
  }

  /**
   * Get existing or create new H2OContext
   *
   * @param conf H2O configuration
   * @return H2O Context
   */
  public static JavaH2OContext getOrCreate(H2OConf conf) {
    return new JavaH2OContext(H2OContext.getOrCreate(conf));
  }

  private static JavaH2OContext deprecationWarn(
      JavaH2OContext jhc, String oldParams, String newParams) {
    jhc.hc
        .log()
        .warn(
            "Method 'getOrCreate("
                + oldParams
                + ")' is deprecated and will be removed in release 3.32. Use"
                + "'getOrCreate("
                + newParams
                + ")' instead!");
    return jhc;
  }

  public static JavaH2OContext getOrCreate(JavaSparkContext jsc) {
    return deprecationWarn(getOrCreate(), "JavaSparkContext jsc", "");
  }

  public static JavaH2OContext getOrCreate(JavaSparkContext jsc, H2OConf conf) {
    return deprecationWarn(getOrCreate(), "JavaSparkContext jsc, H2OConf conf", "H2OConf conf");
  }

  public static JavaH2OContext getOrCreate(SparkSession sparkSession) {
    return deprecationWarn(getOrCreate(), "SparkSession sparkSession", "");
  }

  public static JavaH2OContext getOrCreate(SparkSession sparkSession, H2OConf conf) {
    return deprecationWarn(
        getOrCreate(), "SparkSession sparkSession, H2OConf conf", "H2OConf conf");
  }

  public String toString() {
    return hc.toString();
  }

  public String h2oLocalClient() {
    return hc.h2oLocalClient();
  }

  public String h2oLocalClientIp() {
    return hc.h2oLocalClientIp();
  }

  public int h2oLocalClientPort() {
    return hc.h2oLocalClientPort();
  }

  public void stop(boolean stopSparkContext) {
    hc.stop(stopSparkContext);
  }

  public void openFlow() {
    hc.openFlow();
  }

  /**
   * Return a copy of this JavaH2OContext's configuration. The configuration ''cannot'' be changed
   * at runtime.
   *
   * @return Sparkling Water configuration
   */
  public H2OConf getConf() {
    return hc.getConf();
  }

  /**
   * Conversion from RDD[String] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDString(JavaRDD<String> rdd, String frameName) {
    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDString(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Boolean] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDBool(JavaRDD<Boolean> rdd, String frameName) {

    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaBool(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Integer] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDInt(JavaRDD<Integer> rdd, String frameName) {
    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaInt(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Byte] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDByte(JavaRDD<Byte> rdd, String frameName) {

    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaByte(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Short] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDShort(JavaRDD<Short> rdd, String frameName) {

    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaShort(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Float] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDFloat(JavaRDD<Float> rdd, String frameName) {

    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaFloat(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Double] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDDouble(JavaRDD<Double> rdd, String frameName) {
    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaDouble(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[Long] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDLong(JavaRDD<Long> rdd, String frameName) {
    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaLong(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[LabeledPoint] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDLabeledPoint(JavaRDD<LabeledPoint> rdd, String frameName) {
    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDLabeledPoint(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Conversion from RDD[java.sql.TimeStamp] to H2O's DataFrame
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return new H2O Frame
   */
  public H2OFrame asH2OFrameFromRDDTimeStamp(JavaRDD<java.sql.Timestamp> rdd, String frameName) {
    return hc.asH2OFrame(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDTimeStamp(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[String]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDStringKey(JavaRDD<String> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDString(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Boolean]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDBoolKey(JavaRDD<Boolean> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaBool(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Integer]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDIntKey(JavaRDD<Integer> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaInt(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Byte]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDByteKey(JavaRDD<Byte> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaByte(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Short]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDShortKey(JavaRDD<Short> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaShort(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Float]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDFloatKey(JavaRDD<Float> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaFloat(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Double]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDDoubleKey(JavaRDD<Double> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaDouble(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[Long]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDLongKey(JavaRDD<Long> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaLong(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[LabeledPoint]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDLabeledPointKey(JavaRDD<LabeledPoint> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDLabeledPoint(rdd.rdd()),
            Option.apply(frameName));
  }

  /**
   * Returns key of the H2O's DataFrame conversed from RDD[java.sql.TimeStamp]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public Key<Frame> asH2OFrameFromRDDTimeStampKey(
      JavaRDD<java.sql.Timestamp> rdd, String frameName) {
    return (Key<Frame>)
        hc.toH2OFrameKey(
            SupportedRDD$.MODULE$.toH2OFrameFromRDDTimeStamp(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[String]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDStringKeyString(JavaRDD<String> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDString(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Boolean]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDBoolKeyString(JavaRDD<Boolean> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaBool(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Integer]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDIntKeyString(JavaRDD<Integer> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaInt(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Byte]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDByteKeyString(JavaRDD<Byte> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaByte(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Short]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDShortKeyString(JavaRDD<Short> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaShort(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Float]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDFloatKeyString(JavaRDD<Float> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaFloat(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Double]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDDoubleKeyString(JavaRDD<Double> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaDouble(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[Long]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDLongKeyString(JavaRDD<Long> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDJavaLong(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[LabeledPoint]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDLabeledPointKeyString(
      JavaRDD<LabeledPoint> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDLabeledPoint(rdd.rdd()), Option.apply(frameName));
  }

  /**
   * Returns key string of the H2O's DataFrame conversed from RDD[java.sql.TimeStamp]
   *
   * @param rdd rdd to convert
   * @param frameName frame name
   * @return Key string of the created H2O Frame
   */
  @SuppressWarnings("unchecked")
  public String asH2OFrameFromRDDTimeStampKeyString(
      JavaRDD<java.sql.Timestamp> rdd, String frameName) {
    return hc.asH2OFrameKeyString(
        SupportedRDD$.MODULE$.toH2OFrameFromRDDTimeStamp(rdd.rdd()), Option.apply(frameName));
  }
}
