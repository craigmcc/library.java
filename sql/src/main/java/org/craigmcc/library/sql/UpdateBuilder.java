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
 * <p>Builder that generates a {@link PreparedStatement} for an SQL UPDATE.</p>
 *
 * <p>TODO - examples</p>
 *
 * <p><strong>USAGE NOTES:</strong></p>
 * <ul>
 *     <li>You may only utilize decorator methods that are marked as being
 *         relevant for UPDATE statements, or exist only in this class.</li>
 *     <li>Be sure to call <code>close()</code> on the <code>PreparedStatement</code>
 *         when you are done with it.</li>
 * </ul>
 */
public class UpdateBuilder extends MutatingStatementBuilder<UpdateBuilder>
        implements StatementBuilder {

    // Constructors ----------------------------------------------------------

    public UpdateBuilder(@NotNull String table) {
        super(table);
    }

    // Public Methods --------------------------------------------------------

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {

        if (pairs.size() < 1) {
            throw new IllegalArgumentException("At least one column+value pair must be specified");
        }
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
                addParam(pair.value);
            }
        }

        addWhere(sb);
        sql = sb.toString();
        PreparedStatement statement = connection.prepareStatement(sql);
        applyParams(statement);
        return statement;

    }

}
