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

import static org.craigmcc.library.model.Constants.ID_COLUMN;
import static org.craigmcc.library.sql.SqlOperator.GE;
import static org.craigmcc.library.sql.SqlOperator.LT;
import static org.craigmcc.library.sql.SqlOperator.NE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class UpdateBuilderUnitTest extends AbstractUnitTest {

    @Test
    public void update() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .pair("firstName", "Pebbles")
                .pair("lastName", "Flintstone")
                .primary("id", 42L);
        PreparedStatement statement = builder.build(connection);
        System.out.println("update: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET firstName = ?, lastName = ?" +
                        " WHERE (id = 42)"));
    }

    @Test
    public void updateWithAll() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .all()
                .pairLiteral("points", "(points + 100)");
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithAll: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET points = (points + 100)"));
    }

    @Test
    public void updateWithoutAll() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .pairLiteral("points", "(points + 100)");
        assertThrows(IllegalStateException.class,
                () -> builder.build(connection));
    }

    @Test
    public void updateWithAnd() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .pair("firstName", "Betty")
                .pair("lastName", "Rubble")
                .expression("firstName", GE, "'Fred'")
                .expression("points", LT, 100);
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithAnd: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                " SET firstName = ?, lastName = ?" +
                " WHERE (firstName >= 'Fred') AND (points < 100)"));
    }

    @Test
    public void updateWithClause() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .pair("firstName", "Betty")
                .pair("lastName", "Rubble")
                .clause("firstName", NE, "lastName");
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithClause: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET firstName = ?, lastName = ?" +
                        " WHERE (firstName <> lastName)"));
    }

    @Test
    public void updateWithLiteral() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .primary(ID_COLUMN, 234L)
                .pair("updated", now)
                .pairLiteral("firstName", "'Betty'")
                .pairLiteral("lastName", "'Rubble'");
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithLiteral: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET updated = ?, firstName = 'Betty', lastName = 'Rubble'" +
                        " WHERE (id = 234)"));
    }

    @Test
    public void updateWithModel() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ConcreteModel model = new ConcreteModel("Bam Bam", "Rubble", 321);
        model.setId(456L);
        model.setPublished(now);
        model.setUpdated(now);
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .pairModel(model)
                .pair("firstName", "Betty")
                .pair("lastName", "Rubble");
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithModel: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET published = ?, updated = ?," +
                        " firstName = ?, lastName = ?" +
                        " WHERE (id = " + model.getId() + ")"));
    }

    @Test
    public void updateWithOr() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .pair("firstName", "Betty")
                .pair("lastName", "Rubble")
                .or()
                .expression("firstName", GE, "'Fred'")
                .expression("points", LT, 100);
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithAnd: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET firstName = ?, lastName = ?" +
                        " WHERE (firstName >= 'Fred') OR (points < 100)"));
    }

    @Test
    public void updateWithPrimary() throws Exception {
        UpdateBuilder builder = new UpdateBuilder(MY_TABLE)
                .primary(ID_COLUMN, 987L)
                .pair("firstName", "Betty")
                .pair("lastName", "Rubble");
        PreparedStatement statement = builder.build(connection);
        System.out.println("updateWithPrimary: " + builder.toString());
        assertThat(builder.sql,
                is("UPDATE " + MY_TABLE +
                        " SET firstName = ?, lastName = ?" +
                        " WHERE (id = 987)"));
    }

}
