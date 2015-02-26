/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.api.batch;

import java.util.List;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;

/**
 * A BatchResponsePart
 * <p> BatchResponsePart represents a distinct part of a Batch Response body. It can be a ChangeSet response or a
 * response to a retrieve request
 * 
 */
public abstract class BatchResponsePart implements BatchParserResult {

  /**
   * Get responses. If a BatchResponsePart is a response to a retrieve request, the list consists of one response.
   * @return a list of {@link ODataResponse}
   */
  public abstract List<ODataResponse> getResponses();

  /**
   * Get the info if a BatchResponsePart is a ChangeSet response
   * @return true or false
   */
  public abstract boolean isChangeSet();

  /**
   * 
   * @param responses a list of {@link ODataResponse}
   * @return a builder object
   */
  public static BatchResponsePartBuilder responses(final List<ODataResponse> responses) {
    return newBuilder().responses(responses);
  }

  /**
   * @param isChangeSet true if a BatchResponsePart is a ChangeSet response
   * @return a builder object
   */
  public static BatchResponsePartBuilder changeSet(final boolean isChangeSet) {
    return newBuilder().changeSet(isChangeSet);
  }

  /**
   * @return returns a new builder object
   */
  public static BatchResponsePartBuilder newBuilder() {
    return BatchResponsePartBuilder.newInstance();
  }

  /**
   * Implementation of the builder pattern to create instances of this type of object.
   * 
   */
  public static abstract class BatchResponsePartBuilder {
    public abstract BatchResponsePart build();

    private static BatchResponsePartBuilder newInstance() {
      return RuntimeDelegate.createBatchResponsePartBuilder();
    }

    public abstract BatchResponsePartBuilder responses(List<ODataResponse> responses);

    public abstract BatchResponsePartBuilder changeSet(boolean isChangeSet);
  }
}
