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
package org.jnosql.artemis.key.query;


import org.jnosql.artemis.key.KeyValueRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.Optional;

class KeyValueCrudRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final KeyValueRepository repository;

    private final DefaultKeyValueCrudRepository crudRepository;

    KeyValueCrudRepositoryProxy(Class<?> repositoryType, KeyValueRepository repository) {
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.repository = repository;
        this.crudRepository = new DefaultKeyValueCrudRepository(typeClass, repository);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        return method.invoke(crudRepository, args);
    }

    class DefaultKeyValueCrudRepository implements KeyValueCrudRepository {

        private final Class<T> typeClass;

        private final KeyValueRepository repository;

        public DefaultKeyValueCrudRepository(Class<T> typeClass, KeyValueRepository repository) {
            this.typeClass = typeClass;
            this.repository = repository;
        }

        @Override
        public Object put(Object entity) throws NullPointerException {
            return repository.put(entity);
        }

        @Override
        public Object put(Object entity, Duration ttl) throws NullPointerException, UnsupportedOperationException {
            return repository.put(entity, ttl);
        }

        @Override
        public Iterable put(Iterable entities) throws NullPointerException {
            return repository.put(entities);
        }

        @Override
        public Iterable put(Iterable entities, Duration ttl) throws NullPointerException, UnsupportedOperationException {
            return repository.put(entities, ttl);
        }

        @Override
        public void remove(Iterable keys) throws NullPointerException {
            repository.remove(keys);
        }

        @Override
        public void remove(Object key) throws NullPointerException {
            repository.remove(key);
        }

        @Override
        public Iterable get(Iterable keys) throws NullPointerException {
            return repository.get(keys, typeClass);
        }

        @Override
        public Optional get(Object key) throws NullPointerException {
            return repository.get(key, typeClass);
        }
    }
}
