/*
 * Copyright 2017 Otavio Santana and others
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
 */
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.logging.Logger;

/**
 * Class the returns a {@link org.jnosql.diana.api.document.DocumentQuery}
 * on {@link DocumentCrudRepositoryProxy}
 */
class DocumentQueryParser {

    private static final Logger LOGGER = Logger.getLogger(DocumentQueryParser.class.getName());

    private static final String PREFIX = "findBy";


    DocumentQuery parse(String methodName, Object[] args, ClassRepresentation classRepresentation) {
        DocumentQuery documentQuery = DocumentQuery.of(classRepresentation.getName());
        String[] tokens = methodName.replace(PREFIX, DocumentQueryParserUtil.EMPTY).split("(?=AND|OR|OrderBy)");
        String className = classRepresentation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(DocumentQueryParserUtil.AND)) {
                index = and(args, documentQuery, index, token, methodName);
            } else if (token.startsWith(DocumentQueryParserUtil.OR)) {
                index = or(args, documentQuery, index, token, methodName);
            } else if (token.startsWith(DocumentQueryParserUtil.ORDER_BY)) {
                sort(documentQuery, token);
            } else {
                DocumentCondition condition = DocumentQueryParserUtil.toCondition(token, index, args, methodName);
                documentQuery.and(condition);
                index++;
            }
        }

        while (index < args.length) {
            Object value = args[index];
            if (Sort.class.isInstance(value)) {
                documentQuery.addSort(Sort.class.cast(value));
            } else if (Pagination.class.isInstance(value)) {
                Pagination pagination = Pagination.class.cast(value);
                documentQuery.setLimit(pagination.getLimit());
                documentQuery.setStart(pagination.getStart());
            } else {
                LOGGER.info(String.format("Ignoring parameter %s on  methodName %s class name %s arg-number: %d",
                        String.valueOf(value), methodName, className, index));
            }
            index++;
        }
        return documentQuery;
    }


    private void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }

    private int or(Object[] args, DocumentQuery documentQuery, int index, String token, String methodName) {
        String field = token.replace(DocumentQueryParserUtil.OR, DocumentQueryParserUtil.EMPTY);
        DocumentCondition condition = DocumentQueryParserUtil.toCondition(field, index, args, methodName);
        documentQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, DocumentQuery documentQuery, int index, String token,
                    String methodName) {
        String field = token.replace(DocumentQueryParserUtil.AND, DocumentQueryParserUtil.EMPTY);
        DocumentCondition condition = DocumentQueryParserUtil.toCondition(field, index, args, methodName);
        documentQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }

    private void sort(DocumentQuery documentQuery, String token) {
        String field = token.replace(DocumentQueryParserUtil.ORDER_BY, DocumentQueryParserUtil.EMPTY);
        if (field.contains("Desc")) {
            documentQuery.addSort(Sort.of(getName(field.replace("Desc", DocumentQueryParserUtil.EMPTY)), Sort.SortType.DESC));
        } else {
            documentQuery.addSort(Sort.of(getName(field.replace("Asc", DocumentQueryParserUtil.EMPTY)), Sort.SortType.ASC));
        }
    }

    private String getName(String token) {
        return String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1));
    }

}
