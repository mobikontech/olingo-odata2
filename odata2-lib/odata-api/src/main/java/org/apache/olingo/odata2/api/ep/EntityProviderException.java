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
package org.apache.olingo.odata2.api.ep;

import org.apache.olingo.odata2.api.exception.MessageReference;
import org.apache.olingo.odata2.api.exception.ODataMessageException;

/**
 * An {@link EntityProviderException} is the base exception for all <code>EntityProvider</code> related exceptions.
 * It extends the {@link ODataMessageException} and provides several {@link MessageReference} for specification of
 * the thrown exception.
 */
public class EntityProviderException extends ODataMessageException {

  private static final long serialVersionUID = 1L;

  /** INVALID_STATE requires no content value */
  public static final MessageReference COMMON = createMessageReference(EntityProviderException.class, "COMMON");
  /** EXCEPTION_OCCURRED requires 1 content value ('exception name') */
  public static final MessageReference EXCEPTION_OCCURRED = createMessageReference(EntityProviderException.class,
      "EXCEPTION_OCCURRED");
  /** INVALIDMAPPING requires 1 content value ('propertyName') */
  public static final MessageReference INVALID_MAPPING = createMessageReference(EntityProviderException.class,
      "INVALID_MAPPING");
  /** INVALID_ENTITYTYPE requires 2 content values ('supplied entity type' and 'content entity type') */
  public static final MessageReference INVALID_ENTITYTYPE = createMessageReference(EntityProviderException.class,
      "INVALID_ENTITYTYPE");
  /** INVALID_COMPLEX_TYPE requires 2 content values ('supplied complex type' and 'content complex type') */
  public static final MessageReference INVALID_COMPLEX_TYPE = createMessageReference(EntityProviderException.class,
      "INVALID_COMPLEX_TYPE");
  /** INVALID_CONTENT requires 2 content values ('invalid tag' and 'parent tag') */
  public static final MessageReference INVALID_CONTENT = createMessageReference(EntityProviderException.class,
      "INVALID_CONTENT");
  /** INVALID_PROPERTY_VALUE requires 1 content value ('invalid value') */
  public static final MessageReference INVALID_PROPERTY_VALUE = createMessageReference(EntityProviderException.class,
      "INVALID_PROPERTY_VALUE");
  /** MISSING_PROPERTY requires 1 content value ('invalid value') */
  public static final MessageReference MISSING_PROPERTY = createMessageReference(EntityProviderException.class,
      "MISSING_PROPERTY");
  /** INVALID_PARENT_TAG requires 2 content values ('missing attribute name' and 'tag name') */
  public static final MessageReference MISSING_ATTRIBUTE = createMessageReference(EntityProviderException.class,
      "MISSING_ATTRIBUTE");
  /** MISSING_TAG requires 1 content values ('tag name') */
  public static final MessageReference MISSING_TAG = createMessageReference(EntityProviderException.class,
      "MISSING_TAG");
  /** UNSUPPORTED_PROPERTY_TYPE requires 1 content values ('property type') */
  public static final MessageReference UNSUPPORTED_PROPERTY_TYPE = createMessageReference(
      EntityProviderException.class, "UNSUPPORTED_PROPERTY_TYPE");
  public static final MessageReference INLINECOUNT_INVALID = createMessageReference(EntityProviderException.class,
      "INLINECOUNT_INVALID");
  /** INVALID_STATE requires 1 content value ('message') */
  public static final MessageReference INVALID_STATE = createMessageReference(EntityProviderException.class,
      "INVALID_STATE");
  /** INVALID_INLINE_CONTENT requires 1 content value ('invalid inline message') */
  public static final MessageReference INVALID_INLINE_CONTENT = createMessageReference(EntityProviderException.class,
      "INVALID_INLINE_CONTENT");
  /** INVALID_PROPERTY requires 1 content value ('invalid property name') */
  public static final MessageReference INVALID_PROPERTY = createMessageReference(EntityProviderException.class,
      "INVALID_PROPERTY");
  /** ILLEGAL_ARGUMENT requires 1 content value ('message') */
  public static final MessageReference ILLEGAL_ARGUMENT = createMessageReference(EntityProviderException.class,
      "ILLEGAL_ARGUMENT");
  /** INVALID_NAMESPACE requires 1 content value ('invalid tag/namespace') */
  public static final MessageReference INVALID_NAMESPACE = createMessageReference(EntityProviderException.class,
      "INVALID_NAMESPACE");
  /** INVALID_PARENT_TAG requires 2 content values ('expected parent tag' and 'found parent tag') */
  public static final MessageReference INVALID_PARENT_TAG = createMessageReference(EntityProviderException.class,
      "INVALID_PARENT_TAG");
  public static final MessageReference EXPANDNOTSUPPORTED = createMessageReference(EntityProviderException.class,
      "EXPANDNOTSUPPORTED");
  /** DOUBLE_PROPERTY requires 1 content value ('double tag/property') */
  public static final MessageReference DOUBLE_PROPERTY = createMessageReference(EntityProviderException.class,
      "DOUBLE_PROPERTY");
  /** NOT_SET_CHARACTER_ENCODING requires no content value */
  public static final MessageReference NOT_SET_CHARACTER_ENCODING = createMessageReference(
      EntityProviderException.class, "NOT_SET_CHARACTER_ENCODING");
  /** UNSUPPORTED_CHARACTER_ENCODING requires 1 content value ('found but unsupported character encoding') */
  public static final MessageReference UNSUPPORTED_CHARACTER_ENCODING = createMessageReference(
      EntityProviderException.class, "UNSUPPORTED_CHARACTER_ENCODING");
  /** MEDIA_DATA_NOT_INITIAL requires no content value */
  public static final MessageReference MEDIA_DATA_NOT_INITIAL = createMessageReference(EntityProviderException.class,
      "MEDIA_DATA_NOT_INITIAL");
  /** END_DOCUMENT_EXPECTED requires 1 content value ('actual token') */
  public static final MessageReference END_DOCUMENT_EXPECTED = createMessageReference(EntityProviderException.class,
      "END_DOCUMENT_EXPECTED");
  /** MISSING_RESULTS_ARRAY requires no content value */
  public static final MessageReference MISSING_RESULTS_ARRAY = createMessageReference(EntityProviderException.class,
      "MISSING_RESULTS_ARRAY");

  public static final MessageReference INVALID_DELETED_ENTRY_METADATA = createMessageReference(
      EntityProviderException.class, "INVALID_DELETED_ENTRY_METADATA");

  public EntityProviderException(final MessageReference messageReference) {
    super(messageReference);
  }

  public EntityProviderException(final MessageReference messageReference, final Throwable cause) {
    super(messageReference, cause);
  }

  public EntityProviderException(final MessageReference messageReference, final String errorCode) {
    super(messageReference, errorCode);
  }

  public EntityProviderException(final MessageReference messageReference, final Throwable cause,
      final String errorCode) {
    super(messageReference, cause, errorCode);
  }
}
