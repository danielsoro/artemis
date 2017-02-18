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
package org.jnosql.artemis.document;

import org.jnosql.diana.api.document.DocumentEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DocumentEntityPrePersistTest {

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenEntityIsNull() {
        DocumentEntityPrePersist.of(null);
    }

    @Test
    public void shouldReturnInstance() {
        DocumentEntity entity = DocumentEntity.of("collection");
        DocumentEntityPrePersist prePersist = DocumentEntityPrePersist.of(entity);
        assertEquals(entity, prePersist.getEntity());
    }
}