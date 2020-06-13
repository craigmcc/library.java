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

public class SelectBuilder extends AbstractStatementBuilder {

    public static final String COUNT_COLUMN = "count(*)";

    public SelectBuilder(@NotNull String table) {
        super(table);
    }

    private final List<String> columns = new LinkedList<>();
    private int count = -1;
    private boolean distinct = false;
    private int startIndex = 0;
    private final WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {
        return null; // TODO
    }

    // TODO - lots to finish here

    private static class OrderBy {

        OrderBy(@NotNull String column, @NotNull SqlDirection direction) {
            this.column = column;
            this.direction = direction;
        }

        final String column;
        final SqlDirection direction;

    }

}
