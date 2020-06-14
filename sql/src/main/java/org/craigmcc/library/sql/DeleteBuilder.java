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

import static org.craigmcc.library.model.Constants.ID_COLUMN;

/**
 * <p>Builder that generates a {@link PreparedStatement} for an SQL DELETE.</p>
 *
 * <p>TODO - examples</p>
 *
 * <p><strong>USAGE NOTES:</strong></p>
 * <ul>
 *     <li>You may only utilize decorator methods that are marked as being
 *         relevant for DELETE statements, or exist only in this class.</li>
 *     <li>Be sure to call <code>close()</code> on the <code>PreparedStatement</code>
 *         when you are done with it.</li>
 * </ul>
 */
public class DeleteBuilder extends AbstractStatementBuilder<DeleteBuilder> {

    // Constructors ----------------------------------------------------------

    public DeleteBuilder(@NotNull String table) {
        super(table);
    }

    // Public Methods --------------------------------------------------------

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {

        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(tables.get(0));

        addWhere(sb);
        sql = sb.toString();
        PreparedStatement statement = connection.prepareStatement(sql);
        applyParams(statement);
        return statement;

    }

    /**
     * <p>Record the primary key name and value for generating a WHERE condition.</p>
     *
     * @param model Model object from which to extract primary key information
     *
     * @return This builder
     */
    public DeleteBuilder model(Model model) {
        primary(ID_COLUMN, model.getId());
        return this;
    }

}
