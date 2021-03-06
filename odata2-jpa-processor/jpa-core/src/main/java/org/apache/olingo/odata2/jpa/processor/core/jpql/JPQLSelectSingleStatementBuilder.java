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
package org.apache.olingo.odata2.jpa.processor.core.jpql;

import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContextView;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLSelectSingleContextView;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLStatement;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLStatement.JPQLStatementBuilder;
import org.apache.olingo.odata2.jpa.processor.core.ODataExpressionParser;

public class JPQLSelectSingleStatementBuilder extends JPQLStatementBuilder {

  JPQLStatement jpqlStatement;
  private JPQLSelectSingleContextView context;

  public JPQLSelectSingleStatementBuilder(final JPQLContextView context) {
    this.context = (JPQLSelectSingleContextView) context;
  }

  @Override
  public JPQLStatement build() throws ODataJPARuntimeException {
    jpqlStatement = createStatement(createJPQLQuery());
    return jpqlStatement;

  }

  private String createJPQLQuery() throws ODataJPARuntimeException {

    StringBuilder jpqlQuery = new StringBuilder();
    String tableAlias = context.getJPAEntityAlias();
    String fromClause = context.getJPAEntityName() + JPQLStatement.DELIMITER.SPACE + tableAlias;

    jpqlQuery.append(JPQLStatement.KEYWORD.SELECT).append(JPQLStatement.DELIMITER.SPACE);
    jpqlQuery.append(context.getSelectExpression()).append(JPQLStatement.DELIMITER.SPACE);
    jpqlQuery.append(JPQLStatement.KEYWORD.FROM).append(JPQLStatement.DELIMITER.SPACE);
    jpqlQuery.append(fromClause);

    if (context.getKeyPredicates() != null && context.getKeyPredicates().size() > 0) {
      jpqlQuery.append(JPQLStatement.DELIMITER.SPACE);
      jpqlQuery.append(JPQLStatement.KEYWORD.WHERE).append(JPQLStatement.DELIMITER.SPACE);
      jpqlQuery.append(ODataExpressionParser
          .parseKeyPredicates(context.getKeyPredicates(), context.getJPAEntityAlias()));
    }

    return jpqlQuery.toString();

  }

}
