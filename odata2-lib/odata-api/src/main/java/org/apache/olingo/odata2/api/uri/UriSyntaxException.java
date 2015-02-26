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
package org.apache.olingo.odata2.api.uri;

import org.apache.olingo.odata2.api.exception.MessageReference;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;

/**
 * Exception for violation of the OData URI construction rules,
 * resulting in a 400 Bad Request response.
 * 
 */
public class UriSyntaxException extends ODataBadRequestException {

  private static final long serialVersionUID = 1L;

  public static final MessageReference URISYNTAX = createMessageReference(UriSyntaxException.class, "URISYNTAX");
  public static final MessageReference ENTITYSETINSTEADOFENTITY = createMessageReference(UriSyntaxException.class,
      "ENTITYSETINSTEADOFENTITY");
  public static final MessageReference NOTEXT = createMessageReference(UriSyntaxException.class, "NOTEXT");
  public static final MessageReference NOMEDIARESOURCE = createMessageReference(UriSyntaxException.class,
      "NOMEDIARESOURCE");
  public static final MessageReference NONAVIGATIONPROPERTY = createMessageReference(UriSyntaxException.class,
      "NONAVIGATIONPROPERTY");
  public static final MessageReference MISSINGPARAMETER = createMessageReference(UriSyntaxException.class,
      "MISSINGPARAMETER");
  public static final MessageReference MISSINGKEYPREDICATENAME = createMessageReference(UriSyntaxException.class,
      "MISSINGKEYPREDICATENAME");
  public static final MessageReference DUPLICATEKEYNAMES = createMessageReference(UriSyntaxException.class,
      "DUPLICATEKEYNAMES");
  public static final MessageReference DUPLICATESYSTEMQUERYPARAMETES = createMessageReference(UriSyntaxException.class,
      "DUPLICATESYSTEMQUERYPARAMETES");
  public static final MessageReference EMPTYSEGMENT = createMessageReference(UriSyntaxException.class, "EMPTYSEGMENT");
  public static final MessageReference MUSTNOTBELASTSEGMENT = createMessageReference(UriSyntaxException.class,
      "MUSTNOTBELASTSEGMENT");
  public static final MessageReference MUSTBELASTSEGMENT = createMessageReference(UriSyntaxException.class,
      "MUSTBELASTSEGMENT");
  public static final MessageReference INVALIDSEGMENT = createMessageReference(UriSyntaxException.class,
      "INVALIDSEGMENT");
  public static final MessageReference INVALIDVALUE = createMessageReference(UriSyntaxException.class, "INVALIDVALUE");
  public static final MessageReference INVALIDNULLVALUE = createMessageReference(UriSyntaxException.class,
      "INVALIDNULLVALUE");
  public static final MessageReference INVALIDNEGATIVEVALUE = createMessageReference(UriSyntaxException.class,
      "INVALIDNEGATIVEVALUE");
  public static final MessageReference INVALIDRETURNTYPE = createMessageReference(UriSyntaxException.class,
      "INVALIDRETURNTYPE");
  public static final MessageReference INVALIDPROPERTYTYPE = createMessageReference(UriSyntaxException.class,
      "INVALIDPROPERTYTYPE");
  public static final MessageReference INVALIDKEYPREDICATE = createMessageReference(UriSyntaxException.class,
      "INVALIDKEYPREDICATE");
  public static final MessageReference INVALIDSYSTEMQUERYOPTION = createMessageReference(UriSyntaxException.class,
      "INVALIDSYSTEMQUERYOPTION");
  public static final MessageReference INVALIDFILTEREXPRESSION = createMessageReference(UriSyntaxException.class,
      "INVALIDFILTEREXPRESSION");
  public static final MessageReference INVALIDORDERBYEXPRESSION = createMessageReference(UriSyntaxException.class,
      "INVALIDORDERBYEXPRESSION");
  public static final MessageReference LITERALFORMAT =
      createMessageReference(UriSyntaxException.class, "LITERALFORMAT");
  public static final MessageReference UNKNOWNLITERAL = createMessageReference(UriSyntaxException.class,
      "UNKNOWNLITERAL");
  public static final MessageReference INCOMPATIBLELITERAL = createMessageReference(UriSyntaxException.class,
      "INCOMPATIBLELITERAL");
  public static final MessageReference INCOMPATIBLESYSTEMQUERYOPTION = createMessageReference(UriSyntaxException.class,
      "INCOMPATIBLESYSTEMQUERYOPTION");

  public UriSyntaxException(final MessageReference MessageReference) {
    super(MessageReference);
  }

  public UriSyntaxException(final MessageReference messageReference, final Throwable cause) {
    super(messageReference, cause);
  }

  public UriSyntaxException(final MessageReference MessageReference, final String errorCode) {
    super(MessageReference, errorCode);
  }

  public UriSyntaxException(final MessageReference messageReference, final Throwable cause, final String errorCode) {
    super(messageReference, cause, errorCode);
  }
}
