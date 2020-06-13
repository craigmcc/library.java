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
 *     <li>Some of the methods defined below are only relevant for certain
 *         SQL statement types (DELETE, INSERT, SELECT, UPDATE).  Using them
 *         on a different type will cause that information to be ignored.
 *         See the detailed documentation for each method that is only
 *         relevant for some statement types.</li>
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
public class InsertBuilder extends AbstractStatementBuilder<InsertBuilder> {

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
                param(pair.value);
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

}
