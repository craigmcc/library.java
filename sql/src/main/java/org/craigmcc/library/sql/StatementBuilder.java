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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementations can build a prepared statement from a connection.
 */

public interface StatementBuilder {

    // Static Variables ------------------------------------------------------

    /**
     * <p>Generated string for null values in SQL statements.</p>
     */
    String NULL_VALUE = "NULL";

    // Public Methods --------------------------------------------------------

    /**
     * <p></p>Generate and return an JDBC <code>PreparedStatement</code> from this builder,
     * incorporating the paired replacements, where conditions, and order by condition(s)
     * (if any), and any replacement parameters that were specified.</p>
     *
     * @param connection The JDBC connection for which to create the prepared statement
     *
     * @return A <code>PreparedStatement</code> ready to be executed
     *
     * @throws SQLException if a JDBC processing error occurs
     */
    PreparedStatement build(Connection connection) throws SQLException;

}
