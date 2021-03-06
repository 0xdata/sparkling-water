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

package ai.h2o.sparkling.extensions.rest.api

object Paths {
  val CHUNK: String = "/3/Chunk"
  val CHUNK_CATEGORICAL_DOMAINS: String = "/3/ChunkCategoricalDomains"
  val INITIALIZE_FRAME: String = "/3/InitializeFrame"
  val FINALIZE_FRAME: String = "/3/FinalizeFrame"
  val UPLOAD_PLAN: String = "/3/UploadPlan"
  val LOG_LEVEL: String = "/3/LogLevel"
  val SPARKLING_INTERNAL: String = "/3/SparklingInternal/*"
  val SW_AVAILABLE: String = "/3/scalaint" // H2O Flow determines whether we run Sparkling Water
  // based on existence of /3/scalaint endpoint. We therefore expose dummy endpoint on H2O-3 side
  // which ensures that H2O Flow expose additional Sparkling Water related buttons
  val VERIFY_WEB_OPEN: String = "/3/verifyWebOpen"
  val VERIFY_VERSION: String = "/3/verifyVersion"
}
