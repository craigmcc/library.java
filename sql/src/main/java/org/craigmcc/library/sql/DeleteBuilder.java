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

public class DeleteBuilder extends AbstractStatementBuilder<DeleteBuilder> {

    public DeleteBuilder(@NotNull String table) {
        super(table);
    }

    private final WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {
        String sql = toSql();
        PreparedStatement stmt = connection.prepareStatement(sql);
        Utils.apply(stmt, whereClauseBuilder.params());
        return stmt;
    }

    public DeleteBuilder or() {
        whereClauseBuilder.or();
        return this;
    }

    public DeleteBuilder or(boolean or) {
        whereClauseBuilder.or(or);
        return this;
    }

    public String toSql() {
        String where = whereClauseBuilder.build();
        if ("".equals(where)) {
            throw new IllegalArgumentException("No WHERE conditions specified for this DELETE");
        }
        StringBuilder sb = new StringBuilder("DELETE FROM ")
                .append(tables.get(0))
                .append(where);
        return sb.toString();
    }

    public DeleteBuilder where(@NotNull String column, Object value) {
        whereClauseBuilder.where(column, value);
        return this;
    }

    public DeleteBuilder where(@NotNull String column, @NotNull SqlOperator operator, Object value) {
        whereClauseBuilder.where(column, operator, value);
        return this;
    }

    public DeleteBuilder where(@NotNull String column1, Object value1,
                               @NotNull String column2, Object value2) {
        whereClauseBuilder.where(column1, value1, column2, value2);
        return this;
    }

    public DeleteBuilder where(@NotNull String column1, @NotNull SqlOperator operator1, Object value1,
                               @NotNull String column2, @NotNull SqlOperator operator2, Object value2) {
        whereClauseBuilder.where(column1, operator1, value1, column2, operator2, value2);
        return this;
    }

}
