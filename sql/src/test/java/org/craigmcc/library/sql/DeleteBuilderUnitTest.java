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

import static org.craigmcc.library.sql.SqlOperator.GE;
import static org.craigmcc.library.sql.SqlOperator.LT;
import static org.craigmcc.library.sql.SqlOperator.NE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class DeleteBuilderUnitTest extends AbstractUnitTest {

    @Test
    public void deleteWithAll() throws Exception {
        DeleteBuilder builder = new DeleteBuilder(MY_TABLE)
                .all();
        PreparedStatement statement = builder.build(connection);
        System.out.println("deleteWithAll: " + builder.toString());
        assertThat(builder.sql,
                is("DELETE FROM " + MY_TABLE));
    }

    @Test
    public void deleteWithoutAll() throws Exception {
        DeleteBuilder builder = new DeleteBuilder(MY_TABLE);
        assertThrows(IllegalStateException.class,
                () -> builder.build(connection));
    }

    @Test
    public void deleteWithAnd() throws Exception {
        DeleteBuilder builder = new DeleteBuilder("mytable")
                .expression("firstName", GE, "'Fred'")
                .expression("points", LT, 100);
        PreparedStatement statement = builder.build(connection);
        System.out.println("deleteWithAnd: " + builder.toString());
        assertThat(builder.sql,
                is("DELETE FROM " + MY_TABLE +
                        " WHERE (firstName >= 'Fred') AND (points < 100)"));
    }

    @Test
    public void deleteWithClause() throws Exception {
        DeleteBuilder builder = new DeleteBuilder("mytable")
                .clause("firstName", NE, "lastName");
        PreparedStatement statement = builder.build(connection);
        System.out.println("deleteWithClause: " + builder.toString());
        assertThat(builder.sql,
                is("DELETE FROM " + MY_TABLE +
                        " WHERE (firstName <> lastName)"));
    }

    @Test
    public void deleteWithModel() throws Exception {
        ConcreteModel model = new ConcreteModel("Bam Bam", "Rubble", 321);
        model.setId(456L);
        DeleteBuilder builder = new DeleteBuilder(MY_TABLE)
                .model(model);
        PreparedStatement statement = builder.build(connection);
        System.out.println("deleteWithModel: " + builder.toString());
        assertThat(builder.sql,
                is("DELETE FROM " + MY_TABLE + " WHERE (id = 456)"));
    }

    @Test
    public void deleteWithOr() throws Exception {
        DeleteBuilder builder = new DeleteBuilder(MY_TABLE)
                .or()
                .expression("firstName", GE, "'Fred'")
                .expression("points", LT, 100);
        PreparedStatement statement = builder.build(connection);
        System.out.println("deleteWithAnd: " + builder.toString());
        assertThat(builder.sql,
                is("DELETE FROM " + MY_TABLE +
                        " WHERE (firstName >= 'Fred') OR (points < 100)"));
    }

    @Test
    public void deleteWithPrimary() throws Exception {
        DeleteBuilder builder = new DeleteBuilder(MY_TABLE)
                .primary("id", 123);
        PreparedStatement statement = builder.build(connection);
        System.out.println("deleteWithPrimary: " + builder.toString());
        assertThat(builder.sql,
                is("DELETE FROM " + MY_TABLE + " WHERE (id = 123)"));
    }

}
