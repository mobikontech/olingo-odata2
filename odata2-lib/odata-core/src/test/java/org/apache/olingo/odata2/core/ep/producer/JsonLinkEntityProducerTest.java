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
package org.apache.olingo.odata2.core.ep.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.ep.JsonEntityProvider;
import org.apache.olingo.odata2.testutil.fit.BaseTest;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.mock.MockFacade;
import org.junit.Test;

/**
 *  
 */
public class JsonLinkEntityProducerTest extends BaseTest {
  protected static final String BASE_URI = "http://host:80/service/";
  protected static final EntityProviderWriteProperties DEFAULT_PROPERTIES =
      EntityProviderWriteProperties.serviceRoot(URI.create(BASE_URI)).build();

  @Test
  public void omitJsonWrapperOnLink() throws Exception {
    final EdmEntitySet entitySet = MockFacade.getMockEdm().getDefaultEntityContainer().getEntitySet("Employees");
    Map<String, Object> employeeData = new HashMap<String, Object>();
    employeeData.put("EmployeeId", "1");

    EntityProviderWriteProperties properties =
        EntityProviderWriteProperties.fromProperties(DEFAULT_PROPERTIES).omitJsonWrapper(true).build();
    final ODataResponse response = new JsonEntityProvider().writeLink(entitySet, employeeData, properties);
    assertNotNull(response);
    assertNotNull(response.getEntity());

    final String json = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertNotNull(json);
    assertEquals("{\"uri\":\"" + BASE_URI + "Employees('1')\"}", json);
  }

  @Test
  public void serializeEmployeeLink() throws Exception {
    final EdmEntitySet entitySet = MockFacade.getMockEdm().getDefaultEntityContainer().getEntitySet("Employees");
    Map<String, Object> employeeData = new HashMap<String, Object>();
    employeeData.put("EmployeeId", "1");

    final ODataResponse response = new JsonEntityProvider().writeLink(entitySet, employeeData, DEFAULT_PROPERTIES);
    assertNotNull(response);
    assertNotNull(response.getEntity());

    final String json = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertNotNull(json);
    assertEquals("{\"d\":{\"uri\":\"" + BASE_URI + "Employees('1')\"}}", json);
  }

  @Test
  public void serializePhotoLink() throws Exception {
    final EdmEntitySet entitySet = MockFacade.getMockEdm().getEntityContainer("Container2").getEntitySet("Photos");
    Map<String, Object> photoData = new HashMap<String, Object>();
    photoData.put("Id", 1);
    photoData.put("Type", "image/png");

    final ODataResponse response = new JsonEntityProvider().writeLink(entitySet, photoData, DEFAULT_PROPERTIES);
    assertNotNull(response);
    assertNotNull(response.getEntity());

    final String json = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertNotNull(json);
    assertEquals("{\"d\":{\"uri\":\"" + BASE_URI + "Container2.Photos(Id=1,Type='image%2Fpng')\"}}", json);
  }
}
