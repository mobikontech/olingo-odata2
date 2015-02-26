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
package org.apache.olingo.odata2.annotation.processor.core.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmComplexType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

/**
 *  
 */
@EdmComplexType(name = "c_City", namespace = ModelSharedConstants.NAMESPACE_1)
public class City {

  @EdmProperty(facets = @EdmFacets(maxLength = 5))
  private String postalCode;
  @EdmProperty
  private String cityName;

  public City(final String postalCode, final String name) {
    this.postalCode = postalCode;
    cityName = name;
  }

  public void setPostalCode(final String postalCode) {
    this.postalCode = postalCode;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setCityName(final String cityName) {
    this.cityName = cityName;
  }

  public String getCityName() {
    return cityName;
  }

  @Override
  public String toString() {
    return String.format("%s, %s", cityName, postalCode);
  }

}
