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
package org.jnosql.artemis.validation;



/**
 * Validates bean instances. Implementations of this interface must be thread-safe.
 */
public interface ArtemisValidator {


    /**
     * Validate a bean using bean validation
     *
     * @param bean the be to be validated
     * @param <T>  the type
     * @throws NullPointerException       when bean is null
     * @throws ArtemisValidationException when {@link javax.validation.Validator#validate(Object, Class[])}
     *                                    returns a non empty collection
     */
    <T> void validate(T bean) throws NullPointerException, ArtemisValidationException;
}
