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
package org.apache.olingo.odata2.fit.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSet;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSetPart;
import org.apache.olingo.odata2.api.client.batch.BatchPart;
import org.apache.olingo.odata2.api.client.batch.BatchQueryPart;
import org.apache.olingo.odata2.api.client.batch.BatchSingleResponse;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.fit.ref.AbstractRefTest;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.server.ServletType;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ClientBatchTest extends AbstractRefTest {
  public ClientBatchTest(final ServletType servletType) {
    super(servletType);
  }

  private static final String PUT = "PUT";
  private static final String POST = "POST";
  private static final String GET = "GET";
  private static final String BOUNDARY = "batch_123";

  @Test
  public void testSimpleBatch() throws Exception {
    List<BatchPart> batch = new ArrayList<BatchPart>();
    BatchPart request = BatchQueryPart.method(GET).uri("$metadata").build();
    batch.add(request);

    InputStream body = EntityProvider.writeBatchRequest(batch, BOUNDARY);
    String batchRequestBody = StringHelper.inputStreamToString(body, true);
    checkMimeHeaders(batchRequestBody);
    checkBoundaryDelimiters(batchRequestBody);
    assertTrue(batchRequestBody.contains("GET $metadata HTTP/1.1"));

    HttpResponse batchResponse = execute(batchRequestBody);
    InputStream responseBody = batchResponse.getEntity().getContent();
    String contentType = batchResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
    List<BatchSingleResponse> responses = EntityProvider.parseBatchResponse(responseBody, contentType);
    for (BatchSingleResponse response : responses) {
      assertEquals("200", response.getStatusCode());
      assertEquals("OK", response.getStatusInfo());
      assertTrue(response.getBody().contains("<edmx:Edmx Version=\"1.0\""));
      assertEquals("application/xml;charset=utf-8", response.getHeader(HttpHeaders.CONTENT_TYPE));
      assertNotNull(response.getHeader(HttpHeaders.CONTENT_LENGTH));
    }
  }

  @Test
  public void testChangeSetBatch() throws Exception {
    List<BatchPart> batch = new ArrayList<BatchPart>();

    BatchChangeSet changeSet = BatchChangeSet.newBuilder().build();
    Map<String, String> changeSetHeaders = new HashMap<String, String>();
    changeSetHeaders.put("content-type", "application/json;odata=verbose");

    BatchChangeSetPart changeRequest = BatchChangeSetPart.method(PUT)
        .uri("Employees('2')/EmployeeName")
        .body("{\"EmployeeName\":\"Frederic Fall MODIFIED\"}")
        .headers(changeSetHeaders)
        .build();
    changeSet.add(changeRequest);
    batch.add(changeSet);

    BatchPart request = BatchQueryPart.method(GET)
        .uri("Employees('2')/EmployeeName/$value")
        .build();
    batch.add(request);

    InputStream body = EntityProvider.writeBatchRequest(batch, BOUNDARY);
    String bodyAsString = StringHelper.inputStreamToString(body, true);
    checkMimeHeaders(bodyAsString);
    checkBoundaryDelimiters(bodyAsString);

    assertTrue(bodyAsString.contains("PUT Employees('2')/EmployeeName HTTP/1.1"));
    assertTrue(bodyAsString.contains("GET Employees('2')/EmployeeName/$value HTTP/1.1"));
    assertTrue(bodyAsString.contains("content-type: application/json;odata=verbose"));

    HttpResponse batchResponse = execute(bodyAsString);
    InputStream responseBody = batchResponse.getEntity().getContent();
    String contentType = batchResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
    List<BatchSingleResponse> responses = EntityProvider.parseBatchResponse(responseBody, contentType);
    for (BatchSingleResponse response : responses) {
      if ("204".equals(response.getStatusCode())) {
        assertEquals("No Content", response.getStatusInfo());
      } else if ("200".equals(response.getStatusCode())) {
        assertEquals("OK", response.getStatusInfo());
        assertTrue(response.getBody().contains("Frederic Fall MODIFIED"));
      } else {
        fail();
      }
    }

  }

  @Test
  public void testContentIdReferencing() throws Exception {
    List<BatchPart> batch = new ArrayList<BatchPart>();
    BatchChangeSet changeSet = BatchChangeSet.newBuilder().build();
    Map<String, String> changeSetHeaders = new HashMap<String, String>();
    changeSetHeaders.put("content-type", "application/octet-stream");
    changeSetHeaders.put("Accept", "application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1");
    BatchChangeSetPart changeRequest = BatchChangeSetPart.method(POST)
        .uri("Employees")
        .contentId("1")
        .body("gAAAAgABwESAAMAAAABAAEA")
        .headers(changeSetHeaders)
        .build();
    changeSet.add(changeRequest);

    changeSetHeaders = new HashMap<String, String>();
    changeSetHeaders.put("content-type", "application/json;odata=verbose");
    BatchChangeSetPart changeRequest2 = BatchChangeSetPart.method(PUT)
        .uri("$1/EmployeeName")
        .contentId("2")
        .body("{\"EmployeeName\":\"Frederic Fall MODIFIED\"}")
        .headers(changeSetHeaders)
        .build();
    changeSet.add(changeRequest2);
    batch.add(changeSet);

    Map<String, String> getRequestHeaders = new HashMap<String, String>();
    getRequestHeaders.put("content-id", "3");
    BatchPart request = BatchQueryPart.method(GET)
        .uri("Employees('7')/EmployeeName")
        .contentId("3")
        .headers(getRequestHeaders).build();
    batch.add(request);

    InputStream body = EntityProvider.writeBatchRequest(batch, BOUNDARY);
    String bodyAsString = StringHelper.inputStreamToString(body, true);
    checkMimeHeaders(bodyAsString);
    checkBoundaryDelimiters(bodyAsString);
    assertTrue(bodyAsString.contains("POST Employees HTTP/1.1"));
    assertTrue(bodyAsString.contains("PUT $1/EmployeeName"));
    assertTrue(bodyAsString.contains("GET Employees('7')/EmployeeName HTTP/1.1"));

    HttpResponse batchResponse = execute(bodyAsString);
    InputStream responseBody = batchResponse.getEntity().getContent();
    String contentType = batchResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
    List<BatchSingleResponse> responses = EntityProvider.parseBatchResponse(responseBody, contentType);
    for (BatchSingleResponse response : responses) {
      if ("1".equals(response.getContentId())) {
        assertEquals("201", response.getStatusCode());
        assertEquals("Created", response.getStatusInfo());
      } else if ("2".equals(response.getContentId())) {
        assertEquals("204", response.getStatusCode());
        assertEquals("No Content", response.getStatusInfo());
      } else if ("3".equals(response.getContentId())) {
        assertEquals("200", response.getStatusCode());
        assertEquals("OK", response.getStatusInfo());
      } else {
        fail();
      }
    }
  }

  @Test
  public void testErrorBatch() throws Exception {
    List<BatchPart> batch = new ArrayList<BatchPart>();
    BatchPart request = BatchQueryPart.method(GET)
        .uri("nonsense")
        .build();
    batch.add(request);

    InputStream body = EntityProvider.writeBatchRequest(batch, BOUNDARY);
    String bodyAsString = StringHelper.inputStreamToString(body, true);
    checkMimeHeaders(bodyAsString);
    checkBoundaryDelimiters(bodyAsString);

    assertTrue(bodyAsString.contains("GET nonsense HTTP/1.1"));
    HttpResponse batchResponse = execute(bodyAsString);
    InputStream responseBody = batchResponse.getEntity().getContent();
    String contentType = batchResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
    List<BatchSingleResponse> responses = EntityProvider.parseBatchResponse(responseBody, contentType);
    for (BatchSingleResponse response : responses) {
      assertEquals("404", response.getStatusCode());
      assertEquals("Not Found", response.getStatusInfo());
    }
  }

  private HttpResponse execute(final String body) throws Exception {
    final HttpPost post = new HttpPost(URI.create(getEndpoint().toString() + "$batch"));

    post.setHeader("Content-Type", "multipart/mixed;boundary=" + BOUNDARY);
    HttpEntity entity = new StringEntity(body);
    post.setEntity(entity);
    HttpResponse response = getHttpClient().execute(post);

    assertNotNull(response);
    assertEquals(202, response.getStatusLine().getStatusCode());

    return response;
  }

  private void checkMimeHeaders(final String requestBody) {
    assertTrue(requestBody.contains("Content-Type: application/http"));
    assertTrue(requestBody.contains("Content-Transfer-Encoding: binary"));
  }

  private void checkBoundaryDelimiters(final String requestBody) {
    assertTrue(requestBody.contains("--" + BOUNDARY));
    assertTrue(requestBody.contains("--" + BOUNDARY + "--"));
  }
}
