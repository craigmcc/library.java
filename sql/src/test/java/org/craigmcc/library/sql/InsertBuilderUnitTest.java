/*
 * Copyright 2020 craigmcc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.craigmcc.library.sql;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class InsertBuilderUnitTest extends AbstractUnitTest {

    @Test
    public void insert() throws Exception {
        InsertBuilder builder = new InsertBuilder(MY_TABLE)
                .pair("firstName", "Fred")
                .pair("lastName", "Flintstone")
                .pair("points", 111);
        PreparedStatement statement = builder.build(connection);
        System.out.println("insert: " + builder.toString());
        assertThat(builder.sql,
                is("INSERT INTO " + MY_TABLE +
                        " (firstName, lastName, points) VALUES (?, ?, ?)"));
    }

    @Test
    public void insertWithLiteral() throws Exception {
        InsertBuilder builder = new InsertBuilder(MY_TABLE)
                .pairLiteral("firstName", "'Wilma'")
                .pair("lastName", "Flintstone")
                .pairLiteral("points", 222);
        PreparedStatement statement = builder.build(connection);
        System.out.println("insertWithLiteral: " + builder.toString());
        assertThat(builder.sql,
                is("INSERT INTO " + MY_TABLE +
                " (firstName, lastName, points) VALUES ('Wilma', ?, 222)"));
    }

    @Test
    public void insertWithModel() throws Exception {
        ConcreteModel model = new ConcreteModel("Barney", "Rubble", 123);
        model.setId(123L);
        model.setPublished(LocalDateTime.now());
        model.setUpdated(model.getPublished());
        InsertBuilder builder = new InsertBuilder(MY_TABLE)
                .pairModel(model)
                .pair("firstName", "Barney")
                .pair("lastName", "Rubble")
                .pair("points", 333);
        PreparedStatement statement = builder.build(connection);
        System.out.println("insertWithModel: " + builder.toString());
        assertThat(builder.sql,
                is("INSERT INTO " + MY_TABLE +
                        " (published, updated, firstName, lastName, points)" +
                        " VALUES (?, ?, ?, ?, ?)"));
    }

    @Test
    public void insertWithPrimaryKey() throws Exception {
        InsertBuilder builder = new InsertBuilder(MY_TABLE)
                .pair("firstName", "Fred")
                .pair("lastName", "Flintstone")
                .primary("id"); // Should be ignored
        PreparedStatement statement = builder.build(connection);
        System.out.println("insertWithPrimaryKey: " + builder.toString());
        assertThat(builder.sql,
                is("INSERT INTO " + MY_TABLE +
                        " (firstName, lastName) VALUES (?, ?)"));
    }

}
