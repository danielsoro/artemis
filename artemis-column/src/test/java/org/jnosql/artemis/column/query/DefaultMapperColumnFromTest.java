/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.column.query;

import org.hamcrest.Matchers;
import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.column.ColumnQueryMapperBuilder;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.Worker;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnFrom;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(CDIJUnitRunner.class)
public class DefaultMapperColumnFromTest {

    @Inject
    private ColumnQueryMapperBuilder mapperBuilder;


    @Test
    public void shouldReturnSelectStarFrom() {
        ColumnFrom columnFrom = mapperBuilder.selectFrom(Person.class);
        ColumnQuery query = columnFrom.build();
        ColumnQuery queryExpected = ColumnQueryBuilder.select().from("Person").build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectOrderAsc() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).orderBy("salary").asc().build();
        ColumnQuery queryExpected = select().from("Worker").orderBy("money").asc().build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectOrderDesc() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).orderBy("salary").desc().build();
        ColumnQuery queryExpected = select().from("Worker").orderBy("money").desc().build();
        Assert.assertEquals(queryExpected, query);
    }


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorSelectWhenOrderIsNull() {
        mapperBuilder.selectFrom(Worker.class).orderBy(null);
    }

    @Test
    public void shouldSelectLimit() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).limit(10).build();
        ColumnQuery queryExpected = select().from("Worker").limit(10).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectStart() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).start(10).build();
        ColumnQuery queryExpected = select().from("Worker").start(10).build();
        Assert.assertEquals(queryExpected, query);
    }



    @Test
    public void shouldSelectWhereNameEq() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("name").eq("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("name").eq("Ada").build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameLike() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("name").like("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("name").like("Ada").build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameGt() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").gt(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").gt(10).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameGte() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").gte(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").gte(10).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameLt() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").lt(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").lt(10).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameLte() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").lte(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").lte(10).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameBetween() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").between(10, 20).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").between(10, 20).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameNot() {
        String columnFamily = "columnFamily";
        String name = "Ada Lovelace";
        ColumnQuery query = select().from(columnFamily).where("name").not().eq(name).build();
        ColumnCondition condition = query.getCondition().get();

        Column column = condition.getColumn();
        ColumnCondition negate = column.get(ColumnCondition.class);
        assertTrue(query.getColumns().isEmpty());
        assertEquals(columnFamily, query.getColumnFamily());
        assertEquals(Condition.NOT, condition.getCondition());
        assertEquals(Condition.EQUALS, negate.getCondition());
        assertEquals("name", negate.getColumn().getName());
        assertEquals(name, negate.getColumn().get());
    }


    @Test
    public void shouldSelectWhereNameAnd() {
        String columnFamily = "columnFamily";
        String name = "Ada Lovelace";
        ColumnQuery query = select().from(columnFamily).where("name").eq(name).and("age").gt(10).build();
        ColumnCondition condition = query.getCondition().get();

        Column column = condition.getColumn();
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertEquals(Condition.AND, condition.getCondition());
        assertThat(conditions, containsInAnyOrder(ColumnCondition.eq(Column.of("name", name)),
                ColumnCondition.gt(Column.of("age", 10))));
    }

    @Test
    public void shouldSelectWhereNameOr() {
        String columnFamily = "columnFamily";
        String name = "Ada Lovelace";
        ColumnQuery query = select().from(columnFamily).where("name").eq(name).or("age").gt(10).build();
        ColumnCondition condition = query.getCondition().get();

        Column column = condition.getColumn();
        List<ColumnCondition> conditions = column.get(new TypeReference<List<ColumnCondition>>() {
        });
        assertEquals(Condition.OR, condition.getCondition());
        assertThat(conditions, containsInAnyOrder(ColumnCondition.eq(Column.of("name", name)),
                ColumnCondition.gt(Column.of("age", 10))));
    }


}