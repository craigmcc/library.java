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

import org.craigmcc.library.model.Model;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>Builder that generates a {@link PreparedStatement} for an SQL INSERT.</p>
 *
 * <p><strong>EXAMPLE 1:</strong></p>
 *
 * <code>
 *     InsertBuilder builder = new InsertBuilder("mytable")
 *       .pair("firstName", "Fred")
 *       .pair("lastName", "Flintstone")
 *       .build(connection);
 * </code>
 *
 * <p>will result in PreparedStatement:
 * <code>
 *     INSERT INTO mytable (firstName, lastName) VALUES (?, ?)
 * </code></p>
 *
 * <p><strong>EXAMPLE 2:</strong></p>
 *
 * <code>
 *     InsertBuilder builder = new InsertBuilder("mytable")
 *       .literal("firstName", "Fred")
 *       .pair("lastName", "Flintstone")
 *       .build(connection);
 * </code>
 *
 * <p>will result in PreparedStatement:
 * <code>
 *     INSERT INTO mytable (firstName, lastName) VALUES ('Fred', ?)
 * </code></p>
 *
 * <p><strong>USAGE NOTES:</strong></p>
 * <ul>
 *     <li>You may only utilize decorator methods that are marked as being
 *         relevant for INSERT statements, or exist only in this class.</li>
 *     <li>In order to to retrieve the generated primary key for the new row,
 *         you will need to save a reference to the generated
 *         <code>PreparedStatement</code>, and call <code>getGeneratedKeys()</code>
 *         on it after the insert completes.  In the resulting <code>ResultSet</code>,
 *         call <code>next()</code> and then <code>getLong(1)</code> (or whatever
 *         is appropriate for the data type of your primary key) to retrieve it.</li>
 *     <li>Be sure to call <code>close()</code> on the <code>PreparedStatement</code>
 *         when you are done with it.</li>
 * </ul>
 */
public class InsertBuilder extends MutatingStatementBuilder<InsertBuilder>
        implements StatementBuilder {

    // Constructors ----------------------------------------------------------

    public InsertBuilder(@NotNull String table) {
        super(table);
    }

    // Public Methods --------------------------------------------------------

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {

        StringBuilder sb = new StringBuilder("INSERT INTO ")
                .append(tables.get(0))
                .append(" (");
        boolean first = true;
        for (Pair pair : pairs) {
            if ((primary != null) && pair.column.equals(primary.column)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(pair.column);
        }

        sb.append(") VALUES (");
        first = true;
        for (Pair pair : pairs) {
            if ((primary != null) && pair.column.equals(primary.column)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            if (pair.value == null) {
                sb.append(NULL_VALUE);
            } else if (pair.literal) {
                sb.append(pair.value);
            } else {
                sb.append("?");
                addParam(pair.value);
            }
        }
        sb.append(")");
        sql = sb.toString();

        PreparedStatement statement = null;
        if (primary != null) {
            String[] keys = new String[] { primary.column };
            statement = connection.prepareStatement(sql, keys);
        } else {
            statement = connection.prepareStatement(sql);
        }
        applyParams(statement);
        return statement;

    }

    /**
     * <p>Store the specified name of the primary key column for this object
     * so that it can be retrieved after an INSERT statement is completed.
     *
     * <p><strong>NOTE:</strong> If you are dealing with a {@link Model} object,
     * calling <code>pairModel()</code> will have done this for you already.</p>
     *
     * @param column Name of the primary key column for this table
     *
     * @return This builder
     */
    public InsertBuilder primary(@NotNull String column) {
        this.primary = new Pair(column, null);
        return this;
    }

}
