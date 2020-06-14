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
import java.util.LinkedList;
import java.util.List;

import static org.craigmcc.library.model.Constants.ID_COLUMN;
import static org.craigmcc.library.model.Constants.PUBLISHED_COLUMN;
import static org.craigmcc.library.model.Constants.UPDATED_COLUMN;

/**
 * <p>Builder that generates a {@link PreparedStatement} for an SQL SELECT.</p>
 *
 * <p>TODO - examples</p>
 */
public class SelectBuilder extends AbstractStatementBuilder<SelectBuilder> {

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
        if (distinct) {
            sb.append("DISTINCT ");
        }
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
        addLimit(sb);
        addOffset(sb);

        sql = sb.toString();
        PreparedStatement statement = connection.prepareStatement(sql);
        applyParams(statement);
        return statement;

    }

    /**
     * <p>Store the name of one or more columns that will be retrieved.</p>
     *
     * @param columns Column name(s) to be retrieved
     *
     * @return This bulder
     */
    public SelectBuilder column(@NotNull String... columns) {
        for (String column : columns) {
            pairs.add(new Pair(column, null));
        }
        return this;
    }

    /**
     * <p>Store the names of the columns in the underlying {@link Model}
     * base class to be retrieved.</p>
     *
     * @param model The model object from which to copy common column names
     *
     * @return This builder
     */
    public SelectBuilder columnModel(@NotNull Model model) {
        column(ID_COLUMN, PUBLISHED_COLUMN, UPDATED_COLUMN);
        return this;
    }

    /**
     * <p>Add the specified limit on the number of rows to be returned.
     * Default is however many rows they are starting from the offset position.</p>
     *
     * @param limit The maximum number of rows to be returned
     *
     * @return This builder
     */
    public SelectBuilder limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * <p>Add the specified offset (number of rows to skip at the beginning
     * of the matched set).  Default is zero (no rows are skipped).</p>
     *
     * @param offset The number of rows to be skipped
     *
     * @return This builder
     */
    public SelectBuilder offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    // Protected Methods -----------------------------------------------------

    /**
     * <p>Add a LIMIT clause, if it was requested.</p>
     *
     * @param sb StringBuilder containing the SQL text being created
     */
    protected void addLimit(StringBuilder sb) {
        if (limit != null) {
            sb.append(" LIMIT " + limit);
        }
    }

    /**
     * <p>Add an OFFSET clause, if it was requested.</p>
     *
     * @param sb StringBuilder containing the SQL text being created
     */
    protected void addOffset(StringBuilder sb) {
        if (offset != null) {
            sb.append(" OFFSET " + offset);
        }
    }

}
