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

public class UpdateBuilder extends AbstractStatementBuilder<UpdateBuilder> {

    // Constructors ----------------------------------------------------------

    public UpdateBuilder(@NotNull String table) {
        super(table);
    }

    private final WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();

    // Public Methods --------------------------------------------------------

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {

        if (pairs.size() < 1) {
            throw new IllegalArgumentException("At least one column+value pair must be specified");
        }
/*
        String where = whereClauseBuilder.build();
        if ("".equals(where)) {
            throw new IllegalArgumentException("No WHERE conditions specified for this UPDATE");
        }
*/

        StringBuilder sb = new StringBuilder("UPDATE ")
                .append(tables.get(0))
                .append(" SET ");
        boolean first = true;
        for (Pair pair : pairs) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(pair.column);
            sb.append(" = ");
            if (pair.value == null) {
                sb.append(NULL_VALUE);
            } else if (pair.literal) {
                sb.append(pair.value);
            } else {
                sb.append("?");
                param(pair.value);
            }
        }

//        TODO - is primary key matching part of a where builder or handled here?
//        sb.append(where); // TODO - append where clause
//        params.addAll(whereClauseBuilder.params()); // TODO - append params from where clause

        sql = sb.toString();
        PreparedStatement statement = connection.prepareStatement(sql);
        if (params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                if (statement != null) { // TODO - Yay Mockito :-(
                    statement.setObject(i + 1, params.get(i));
                }
            }
        }
        return statement;

    }

}
