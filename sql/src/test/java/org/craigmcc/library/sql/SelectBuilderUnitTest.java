package org.craigmcc.library.sql;

import org.junit.Test;

import java.sql.PreparedStatement;

import static org.craigmcc.library.model.Constants.ID_COLUMN;
import static org.craigmcc.library.sql.SqlOperator.GE;
import static org.craigmcc.library.sql.SqlOperator.LT;
import static org.craigmcc.library.sql.SqlOperator.NE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class SelectBuilderUnitTest extends AbstractUnitTest {

    @Test
    public void selectWithAll() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .all()
                .orderBy("lastName", SqlDirection.ASC)
                .orderBy("firstName", SqlDirection.DESC)
                .offset(50)
                .limit(25);
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithAll: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT * FROM " + MY_TABLE +
                        " ORDER BY lastName ASC, firstName DESC" +
                        " LIMIT 25 OFFSET 50"));
    }

    @Test
    public void selectWithoutAll() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE);
        assertThrows(IllegalStateException.class,
                () -> builder.build(connection));
    }

    @Test
    public void selectWithAnd() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .expression("firstName", GE, "'Fred'")
                .expression("points", LT, 100);
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithAnd: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT * FROM " + MY_TABLE +
                        " WHERE (firstName >= 'Fred')" +
                        " AND (points < 100)"));
    }

    @Test
    public void selectWithClause() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .clause("firstName", NE, "lastName");
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithClause: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT * FROM " + MY_TABLE +
                        " WHERE (firstName <> lastName)"));
    }

    @Test
    public void selectWithColumn() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .all()
                .column("firstName", "lastName");
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithColumn: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT firstName, lastName FROM " + MY_TABLE));
    }

    @Test
    public void selectWithGroupBy() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .all()
                .column("firstName", "lastName")
                .groupBy("lastName");
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithColumn: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT firstName, lastName FROM " + MY_TABLE +
                        " GROUP BY lastName"));
    }

    @Test
    public void setWithModel() throws Exception {
        ConcreteModel model = new ConcreteModel("Bam Bam", "Rubble", 567);
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .all()
                .columnModel(model)
                .column("points");
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithModel: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT id, published, updated, points FROM " + MY_TABLE));
    }

    @Test
    public void selectWithOr() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .expression("firstName", GE, "'Fred'")
                .or()
                .expression("points", LT, 100);
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithOr: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT * FROM " + MY_TABLE +
                        " WHERE (firstName >= 'Fred')" +
                        " OR (points < 100)"));
    }

    @Test
    public void selectWithPrimary() throws Exception {
        SelectBuilder builder = new SelectBuilder(MY_TABLE)
                .primary("id", 987);
        PreparedStatement statement = builder.build(connection);
        System.out.println("selectWithPrimary: " + builder.toString());
        assertThat(builder.sql,
                is("SELECT * FROM " + MY_TABLE +
                        " WHERE (id = 987)"));
    }

}
