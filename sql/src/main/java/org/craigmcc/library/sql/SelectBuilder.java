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
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Builder that generates a {@link PreparedStatement} for an SQL SELECT.</p>
 *
 * <p>TODO - examples</p>
 */
public class SelectBuilder extends AbstractStatementBuilder<SelectBuilder> {

    // Instance Variables ----------------------------------------------------

    // TODO - make these available, and move to AbstractStatementBuilder?
    private int count = -1; // Maximum number of rows to match
    private boolean distinct = false;
    private int offset = 0; // Skip this many rows before matching

    // Static Variables ------------------------------------------------------

    // TODO - add a way to utilize this instead of column names?
    public static final String COUNT_LITERAL = "count(*)";

    // Constructors ----------------------------------------------------------

    public SelectBuilder(@NotNull String table) {
        super(table);
    }

    // Public Methods --------------------------------------------------------

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {

        StringBuilder sb = new StringBuilder("SELECT ");
        if (pairs.size() == 0) {
            sb.append("*");
        } else {
            boolean first = true;
            for (Pair pair : pairs) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(pair.column);
            }
        }

        sb.append(" FROM ");
        sb.append(tables.get(0));
        addWhere(sb);
        addGroupBy(sb);
        addOrderBy(sb);

        sql = sb.toString();
        PreparedStatement statement = connection.prepareStatement(sql);
        applyParams(statement);
        return statement;

    }

}
