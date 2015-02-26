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
package org.apache.olingo.odata2.jpa.processor.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmFacets;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.FunctionImport;
import org.apache.olingo.odata2.api.edm.provider.FunctionImportParameter;
import org.apache.olingo.odata2.api.edm.provider.Mapping;
import org.apache.olingo.odata2.api.edm.provider.ReturnType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.jpa.processor.api.access.JPAEdmBuilder;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAModelException;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmComplexTypeView;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmEntityContainerView;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmEntitySetView;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmEntityTypeView;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmMapping;
import org.apache.olingo.odata2.jpa.processor.core.common.ODataJPATestConstants;
import org.apache.olingo.odata2.jpa.processor.core.mock.ODataJPAContextMock;
import org.apache.olingo.odata2.jpa.processor.core.mock.model.JPACustomProcessorMock;
import org.apache.olingo.odata2.jpa.processor.core.mock.model.JPACustomProcessorNegativeMock;
import org.junit.Before;
import org.junit.Test;

public class JPAEdmFunctionImportTest extends JPAEdmTestModelView {
  private static final int METHOD_COUNT = 7;
  private static int VARIANT = 0;
  private JPAEdmFunctionImport jpaEdmfunctionImport;

  @Before
  public void setup() {
    jpaEdmfunctionImport = new JPAEdmFunctionImport(this);
  }

  /**
   * Test Case - Function Import Basic test - Positive Case
   */
  @Test
  public void testFunctionImportBasic() {
    VARIANT = 0;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);
    for (FunctionImport functionImport : functionImportList) {
      assertEquals(functionImport.getName(), "Method1");
      assertNotNull(functionImport.getMapping());
      Mapping mapping = new Mapping();
      mapping.setInternalName("method1");

      assertEquals(mapping.getInternalName(), functionImport.getMapping().getInternalName());

      ReturnType returnType = functionImport.getReturnType();
      assertNotNull(returnType);
      assertEquals(EdmMultiplicity.MANY, returnType.getMultiplicity());

      List<FunctionImportParameter> funcImpList = functionImport.getParameters();
      assertEquals(2, funcImpList.size());
      assertEquals("Param1", funcImpList.get(0).getName());
      assertEquals(EdmSimpleTypeKind.String, funcImpList.get(0).getType());

      assertEquals("Param3", funcImpList.get(1).getName());
      assertEquals(EdmSimpleTypeKind.Double, funcImpList.get(1).getType());

    }

  }

  /**
   * Test Case - Enable a method that does not exists
   */
  @Test
  public void testFunctionImportNoSuchMethod() {
    VARIANT = 1;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 0);

  }

  /**
   * Test Case - Enable all annotated methods in a class as function imports
   */
  @Test
  public void testFunctionImportAllMethods() {
    VARIANT = 2;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(METHOD_COUNT, functionImportList.size());

  }

  /**
   * Test Case - Function Import with no names. Default name is Java method
   * name.
   */
  @Test
  public void testFunctionImportNoName() {
    VARIANT = 3;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    FunctionImport functionImport = functionImportList.get(0);
    assertEquals(functionImport.getName(), "method3");
    assertNotNull(functionImport.getMapping());

    ReturnType returnType = functionImport.getReturnType();
    assertNotNull(returnType);
    assertEquals(EdmMultiplicity.ONE, returnType.getMultiplicity());
    assertEquals(returnType.getTypeName().toString(), EdmSimpleTypeKind.Int32.getFullQualifiedName().toString());
  }

  /**
   * Test Case - Function Import with No return type defined - Negative case
   */
  @Test
  public void testNoReturnType() {
    VARIANT = 4;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 0);

  }

  /**
   * Test Case - Function Import with return type as Entity and Multiplicity
   * as Many but no Entity set defined. --> Negative Case
   */
  @Test
  public void testNoEntitySet() {
    VARIANT = 5;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.FUNC_RETURN_TYPE_ENTITY_NOT_FOUND.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }
  }

  /**
   * Test Case - Function Import with return type as Entity Type but method
   * has returns void. --> Negative Case
   */
  @Test
  public void testNoReturnTypeButAnnotated() {
    VARIANT = 6;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.FUNC_RETURN_TYPE_EXP.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }
  }

  /**
   * Test Case - Function Import that returns an Entity Type with Multiplicity
   * as ONE. -->Positive Case
   */
  @Test
  public void testFunctionImportEntityTypeSingleReturn() {
    VARIANT = 7;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    FunctionImport functionImport = functionImportList.get(0);
    assertEquals(functionImport.getName(), "method7");
    assertNotNull(functionImport.getMapping());
    JPAEdmMapping mapping = (JPAEdmMapping) functionImport.getMapping();
    assertEquals(JPACustomProcessorMock.class, mapping.getJPAType());

    ReturnType returnType = functionImport.getReturnType();
    assertNotNull(returnType);
    assertEquals(EdmMultiplicity.ONE, returnType.getMultiplicity());
    assertEquals(returnType.getTypeName().toString(), ODataJPAContextMock.PERSISTENCE_UNIT_NAME + "."
        + JPACustomProcessorMock.edmName);
  }

  /**
   * Test Case - Function Import that returns an Entity Type that is not found
   * in JPA Model
   */
  @Test
  public void testFunctionImportEntityTypeInvalid() {
    VARIANT = 8;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.TYPE_NOT_SUPPORTED.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }

  }

  /**
   * Test Case - Function Import that returns a complex Type
   */
  @Test
  public void testFunctionImportComplexType() {
    VARIANT = 9;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    FunctionImport functionImport = functionImportList.get(0);
    assertEquals(functionImport.getName(), "method9");
    assertNotNull(functionImport.getMapping());

    ReturnType returnType = functionImport.getReturnType();
    assertNotNull(returnType);
    assertEquals(EdmMultiplicity.ONE, returnType.getMultiplicity());
    assertEquals(returnType.getTypeName().toString(), ODataJPAContextMock.PERSISTENCE_UNIT_NAME + "."
        + JPACustomProcessorMock.edmName);

  }

  /**
   * Test Case - Function Import that returns a complex Type with multiplicity
   * Many
   */
  @Test
  public void testFunctionImportComplexTypeMany() {
    VARIANT = 10;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    FunctionImport functionImport = functionImportList.get(0);
    assertEquals(functionImport.getName(), "method10");
    assertNotNull(functionImport.getMapping());

    ReturnType returnType = functionImport.getReturnType();
    assertNotNull(returnType);
    assertEquals(EdmMultiplicity.MANY, returnType.getMultiplicity());
    assertEquals(returnType.getTypeName().toString(), ODataJPAContextMock.PERSISTENCE_UNIT_NAME + "."
        + JPACustomProcessorMock.edmName);

  }

  /**
   * Test Case - Function Import that returns an Complex Type that is not
   * found in JPA Model
   */
  @Test
  public void testFunctionImportComplexTypeInvalid() {
    VARIANT = 11;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.FUNC_RETURN_TYPE_ENTITY_NOT_FOUND.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }

  }

  /**
   * Test Case - Function Import that returns an scalar Type that is not valid
   */
  @Test
  public void testFunctionImportScalarTypeInvalid() {
    VARIANT = 12;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.TYPE_NOT_SUPPORTED.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }

  }

  /**
   * Test Case - Function Import that takes a parameter with no name
   */
  @Test
  public void testFunctionImportParamNoName() {
    VARIANT = 13;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.FUNC_PARAM_NAME_EXP.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }

  }

  /**
   * Test Case - Function Import test for facets
   */
  @Test
  public void testFunctionImportParamFacets() {
    VARIANT = 14;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    List<FunctionImportParameter> funcImpParamList = functionImportList.get(0).getParameters();
    EdmFacets facets = funcImpParamList.get(0).getFacets();
    assertNotNull(facets);
    assertEquals(2, facets.getMaxLength().intValue());
    assertEquals(true, facets.isNullable());

    facets = funcImpParamList.get(1).getFacets();
    assertNotNull(facets);
    assertEquals(true, facets.isNullable());
    assertEquals(10, facets.getPrecision().intValue());
    assertEquals(2, facets.getScale().intValue());

  }

  /**
   * Test Case - Function Import test for default facets
   */
  @Test
  public void testFunctionImportParamFacetsDefault() {
    VARIANT = 15;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    List<FunctionImportParameter> funcImpParamList = functionImportList.get(0).getParameters();
    EdmFacets facets = funcImpParamList.get(0).getFacets();
    assertNotNull(facets);
    assertNull(facets.getMaxLength());
    assertEquals(true, facets.isNullable());
    assertNull(facets.getPrecision());
    assertNull(facets.getScale());

  }

  /**
   * Test Case - Function Import test for ReturnType.SCALAR but method returns
   * void
   */
  @Test
  public void testWrongReturnTypeScalar() {
    VARIANT = 16;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.FUNC_RETURN_TYPE_EXP.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }

  }

  /**
   * Test Case - Function Import test for ReturnType.COMPLEX but method returns
   * void
   */
  @Test
  public void testWrongReturnTypeComplex() {
    VARIANT = 17;

    try {
      jpaEdmfunctionImport.getBuilder().build();
      fail("Exception Expected");
    } catch (ODataJPAModelException e) {
      assertEquals(ODataJPAModelException.FUNC_RETURN_TYPE_EXP.getKey(), e.getMessageReference().getKey());
    } catch (ODataJPARuntimeException e) {
      fail("Model Exception Expected");
    }

  }

  /**
   * Test Case - Function Import test for ReturnType.COMPLEX where Complex type is non JPA Embeddable Type
   * 
   */
  @Test
  public void testNonJPAReturnTypeComplex() {
    VARIANT = 18;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 1);

    FunctionImport functionImport = functionImportList.get(0);
    assertEquals(functionImport.getName(), "method18");
    assertNotNull(functionImport.getMapping());

    ReturnType returnType = functionImport.getReturnType();
    assertNotNull(returnType);
    assertEquals(EdmMultiplicity.ONE, returnType.getMultiplicity());
    assertEquals(returnType.getTypeName().toString(), ODataJPAContextMock.PERSISTENCE_UNIT_NAME + "."
        + JPACustomProcessorMock.nonJPAEmbeddableType);

  }

  @Test
  public void testNoFunctionImport() {
    VARIANT = 99;

    build();

    List<FunctionImport> functionImportList = jpaEdmfunctionImport.getConsistentFunctionImportList();

    assertEquals(functionImportList.size(), 0);

  }

  @Test
  public void testGetBuilderIdempotent() {
    JPAEdmFunctionImport jpaEdmfunctionImport = new JPAEdmFunctionImport(this);

    JPAEdmBuilder builder1 = jpaEdmfunctionImport.getBuilder();
    JPAEdmBuilder builder2 = jpaEdmfunctionImport.getBuilder();

    assertEquals(builder1.hashCode(), builder2.hashCode());
  }

  @Override
  public HashMap<Class<?>, String[]> getRegisteredOperations() {

    HashMap<Class<?>, String[]> customOperations = new HashMap<Class<?>, String[]>();

    if (VARIANT == 0) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method1" });
    } else if (VARIANT == 1) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "XYX" });
    } else if (VARIANT == 2) {
      customOperations.put(JPACustomProcessorMock.class, null);
    } else if (VARIANT == 3) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method3" });
    } else if (VARIANT == 4) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method4" });
    } else if (VARIANT == 5) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method5" });
    } else if (VARIANT == 6) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method6" });
    } else if (VARIANT == 7) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method7" });
    } else if (VARIANT == 8) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method8" });
    } else if (VARIANT == 9) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method9" });
    } else if (VARIANT == 10) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method10" });
    } else if (VARIANT == 11) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method11" });
    } else if (VARIANT == 12) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method12" });
    } else if (VARIANT == 13) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method13" });
    } else if (VARIANT == 14) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method1" });
    } else if (VARIANT == 15) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method3" });
    } else if (VARIANT == 16) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method16" });
    } else if (VARIANT == 17) {
      customOperations.put(JPACustomProcessorNegativeMock.class, new String[] { "method17" });
    } else if (VARIANT == 18) {
      customOperations.put(JPACustomProcessorMock.class, new String[] { "method18" });
    } else {
      return null;
    }

    return customOperations;
  }

  @Override
  public Schema getEdmSchema() {
    Schema schema = new Schema();
    ComplexType complexType = new ComplexType();
    complexType.setName(JPACustomProcessorMock.nonJPAEmbeddableType);
    List<ComplexType> list = new ArrayList<ComplexType>();
    list.add(complexType);
    schema.setComplexTypes(list);

    return schema;

  }

  @Override
  public JPAEdmEntityContainerView getJPAEdmEntityContainerView() {
    return this;
  }

  @Override
  public JPAEdmEntitySetView getJPAEdmEntitySetView() {
    return this;
  }

  @Override
  public JPAEdmEntityTypeView getJPAEdmEntityTypeView() {
    return this;
  }

  @Override
  public JPAEdmComplexTypeView getJPAEdmComplexTypeView() {
    return this;
  }

  @Override
  public EntityType searchEdmEntityType(final String arg0) {
    if (arg0.equals(JPACustomProcessorMock.class.getSimpleName())) {
      return new EntityType().setName(JPACustomProcessorMock.edmName);
    } else {
      return null;
    }
  }

  @Override
  public ComplexType searchEdmComplexType(final String arg0) {
    if (arg0.equals(JPACustomProcessorMock.class.getName())) {
      return new ComplexType().setName(JPACustomProcessorMock.edmName);
    } else {
      return null;
    }
  }

  @Override
  public String getpUnitName() {
    return ODataJPAContextMock.PERSISTENCE_UNIT_NAME;
  }

  private void build() {
    try {
      jpaEdmfunctionImport.getBuilder().build();
    } catch (ODataJPAModelException e) {
      fail(ODataJPATestConstants.EXCEPTION_MSG_PART_1 + e.getMessage() + ODataJPATestConstants.EXCEPTION_MSG_PART_2);
    } catch (ODataJPARuntimeException e) {
      fail(ODataJPATestConstants.EXCEPTION_MSG_PART_1 + e.getMessage() + ODataJPATestConstants.EXCEPTION_MSG_PART_2);
    }
  }
}
