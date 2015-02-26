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
package org.apache.olingo.odata2.annotation.processor.ref.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

/**
 *  
 */
@EdmEntityType(name = "Building", namespace = ModelSharedConstants.NAMESPACE_1)
@EdmEntitySet(name = "Buildings")
public class Building {
  @EdmKey
  @EdmProperty(type = EdmType.INT32, facets = @EdmFacets(nullable = false))
  private int id;
  @EdmProperty
  private String name;
  @EdmProperty(name = "Image", type = EdmType.BINARY)
  private byte[] image;
  @EdmNavigationProperty(name = "nb_Rooms", toType = Room.class,
      association = "BuildingRooms", toMultiplicity = Multiplicity.MANY)
  private List<Room> rooms = new ArrayList<Room>();

  public String getId() {
    return Integer.toString(id);
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setImage(final byte[] byteArray) {
    image = byteArray;
  }

  public byte[] getImage() {
    if (image == null) {
      return null;
    } else {
      return image.clone();
    }
  }

  public void addRoom(final Room room) {
    rooms.add(room);
  }

  public List<Room> getRooms() {
    return rooms;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj
        || obj != null && getClass() == obj.getClass() && id == ((Building) obj).id;
  }

  @Override
  public String toString() {
    return "{\"Id\":\"" + id + "\",\"Name\":\"" + name + "\",\"Image\":\"" + Arrays.toString(image) + "\"}";
  }
}
