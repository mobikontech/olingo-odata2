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
package org.apache.olingo.odata2.fit.ref;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.core.commons.ContentType;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.server.ServletType;
import org.junit.Test;

/**
 * Tests employing the reference scenario reading function-import output in XML format.
 * 
 */
public class FunctionImportXmlTest extends AbstractRefXmlTest {

  public FunctionImportXmlTest(final ServletType servletType) {
    super(servletType);
  }

  @Test
  public void functionImports() throws Exception {
    HttpResponse response = callUri("EmployeeSearch('1')/ne_Room/Id/$value?q='alter'");
    checkMediaType(response, HttpContentType.TEXT_PLAIN_UTF8);
    checkEtag(response, "W/\"1\"");
    assertEquals("1", getBody(response));

    assertFalse(getBody(callUri("EmployeeSearch?q='-'")).contains("entry"));

    response = callUri("AllLocations", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_XML);
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    String body = getBody(response);
    assertXpathExists("/d:AllLocations/d:element/d:City[d:CityName=\"" + CITY_2_NAME + "\"]", body);
    final HttpResponse metadataResponse = callUri("$metadata"); 
    final EdmEntityContainer entityContainer = EntityProvider.readMetadata(metadataResponse.getEntity().getContent(),
        false).getDefaultEntityContainer();
    getBody(metadataResponse);
    Object result = EntityProvider.readFunctionImport(HttpContentType.APPLICATION_XML,
        entityContainer.getFunctionImport("AllLocations"), StringHelper.encapsulate(body),
        EntityProviderReadProperties.init().build());
    assertNotNull(result);
    final List<?> collection = (List<?>) result;
    @SuppressWarnings("unchecked")
    final Map<String, Object> secondLocation = (Map<String, Object>) collection.get(1);
    @SuppressWarnings("unchecked")
    final Map<String, Object> secondCity = (Map<String, Object>) secondLocation.get("City");
    assertEquals(CITY_2_NAME, secondCity.get("CityName"));

    response = callUri("AllUsedRoomIds", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_XML);
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    body = getBody(response);
    assertXpathExists("/d:AllUsedRoomIds[d:element=\"3\"]", body);
    result = EntityProvider.readFunctionImport(HttpContentType.APPLICATION_XML,
        entityContainer.getFunctionImport("AllUsedRoomIds"), StringHelper.encapsulate(body),
        EntityProviderReadProperties.init().build());
    assertNotNull(result);
    assertEquals(Arrays.asList("1", "2", "3"), result);

    response = callUri("MaximalAge", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_XML);
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(EMPLOYEE_3_AGE, "/d:MaximalAge", getBody(response));

    response = callUri("MostCommonLocation", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_XML);
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(CITY_2_NAME, "/d:MostCommonLocation/d:City/d:CityName", getBody(response));

    checkUri("ManagerPhoto?Id='1'");

    response = callUri("ManagerPhoto/$value?Id='1'");
    checkMediaType(response, IMAGE_JPEG);
    assertNull(response.getFirstHeader(HttpHeaders.ETAG));
    assertNotNull(getBody(response));

    response = callUri("OldestEmployee", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_XML);
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(EMPLOYEE_3_NAME, "/atom:entry/m:properties/d:EmployeeName", getBody(response));

    response = callUri("OldestEmployee?$format=xml");
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(EMPLOYEE_3_NAME, "/atom:entry/m:properties/d:EmployeeName", getBody(response));

    badRequest("AllLocations/$count");
    badRequest("AllUsedRoomIds/$value");
    badRequest("MaximalAge()");
    badRequest("MostCommonLocation/City/CityName");
    badRequest("ManagerPhoto");
    badRequest("OldestEmployee()");
    notFound("ManagerPhoto?Id='2'");
  }

  @Test
  public void functionImportsDefaultAccept() throws Exception {
    HttpResponse response = callUri("EmployeeSearch('1')/ne_Room/Id/$value?q='alter'");
    checkMediaType(response, HttpContentType.TEXT_PLAIN_UTF8);
    checkEtag(response, "W/\"1\"");
    assertEquals("1", getBody(response));

    assertFalse(getBody(callUri("EmployeeSearch?q='-'")).contains("entry"));

    response = callUri("AllLocations");
    checkMediaType(response, ContentType.APPLICATION_ATOM_XML_FEED_CS_UTF_8);
    assertXpathExists("/d:AllLocations/d:element/d:City[d:CityName=\"" + CITY_2_NAME + "\"]", getBody(response));

    response = callUri("AllUsedRoomIds");
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathExists("/d:AllUsedRoomIds[d:element=\"3\"]", getBody(response));

    response = callUri("MaximalAge");
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(EMPLOYEE_3_AGE, "/d:MaximalAge", getBody(response));

    response = callUri("MostCommonLocation");
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(CITY_2_NAME, "/d:MostCommonLocation/d:City/d:CityName", getBody(response));

    checkUri("ManagerPhoto?Id='1'");

    response = callUri("ManagerPhoto/$value?Id='1'");
    checkMediaType(response, IMAGE_JPEG);
    assertNull(response.getFirstHeader(HttpHeaders.ETAG));
    assertNotNull(getBody(response));

    response = callUri("OldestEmployee");
    checkMediaType(response, HttpContentType.APPLICATION_ATOM_XML_ENTRY_UTF8);
    assertXpathEvaluatesTo(EMPLOYEE_3_NAME, "/atom:entry/m:properties/d:EmployeeName", getBody(response));

    response = callUri("OldestEmployee?$format=xml");
    checkMediaType(response, HttpContentType.APPLICATION_XML_UTF8);
    assertXpathEvaluatesTo(EMPLOYEE_3_NAME, "/atom:entry/m:properties/d:EmployeeName", getBody(response));

    badRequest("AllLocations/$count");
    badRequest("AllUsedRoomIds/$value");
    badRequest("MaximalAge()");
    badRequest("MostCommonLocation/City/CityName");
    badRequest("ManagerPhoto");
    badRequest("OldestEmployee()");
    notFound("ManagerPhoto?Id='2'");
  }

  @Override
  public void checkMediaType(final HttpResponse response, final String expectedContentType) {
    checkMediaType(response, ContentType.parse(expectedContentType));
  }

  private void checkMediaType(final HttpResponse response, final ContentType expectedContentType) {
    ContentType responseContentType =
        ContentType.parse(response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
    Assert.assertEquals(expectedContentType, responseContentType);
  }

  @Test
  public void select() throws Exception {
    HttpResponse response = callUri("EmployeeSearch?q='ede'&$select=Age");
    checkMediaType(response, HttpContentType.APPLICATION_ATOM_XML_UTF8 + ";type=feed");
    String body = getBody(response);
    assertXpathEvaluatesTo(EMPLOYEE_2_AGE, "/atom:feed/atom:entry/m:properties/d:Age", body);
    assertXpathNotExists("/atom:feed/atom:entry/m:properties/d:Location", body);
    assertXpathEvaluatesTo("2", "count(/atom:feed/atom:entry/atom:link)", body);

    response = callUri("EmployeeSearch('2')/ne_Room?q='ede'&$select=Seats");
    checkMediaType(response, HttpContentType.APPLICATION_ATOM_XML_UTF8 + ";type=entry");
    body = getBody(response);
    assertXpathEvaluatesTo("5", "/atom:entry/atom:content/m:properties/d:Seats", body);
    assertXpathNotExists("/atom:entry/atom:content/m:properties/d:Id", body);
    assertXpathEvaluatesTo("1", "count(/atom:entry/atom:link)", body);
  }
}
