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
package org.craigmcc.library.model;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Default implementations for convenience methods in the {@link ModelJdbc}
 * interface.</p>
 *
 * @param <M> The {@link Model} class for which this module defines APIs
 */
public abstract class AbstractModelJdbc<M extends Model> implements ModelJdbc<M>, Constants {

    // Public Methods --------------------------------------------------------

    public @NotNull List<M> populateAll(@NotNull ResultSet resultSet)
            throws SQLException {
        List<M> results = new ArrayList<>();
        while (true) {
            M result = populateNext(resultSet);
            if (result == null) {
                return results;
            } else {
                results.add(result);
            }
        }

    }

    // Protected Methods -----------------------------------------------------

    /**
     * <p>Populate the standard {@link Model} fields from the current row
     * of the specified <code>ResultSet</code>, ignoring any fields that are
     * not present.</p>
     *
     * @param model The {@link Model} object being populated
     * @param resultSet The <code>ResultSet</code> from which to extract
     *                 field values
     *
     * @throws SQLException If a JDBC exception occurs
     */
    protected void populateModel(@NotNull Model model,
                                 @NotNull ResultSet resultSet)
            throws SQLException {
        try {
            model.setId(resultSet.getLong(ID_COLUMN));
        } catch (SQLException e) {
            // Ignore
        }
        try {
            model.setPublished(resultSet.getTimestamp(PUBLISHED_COLUMN)
                    .toLocalDateTime());
        } catch (SQLException e) {
            // Ignore
        }
        try {
            model.setUpdated(resultSet.getTimestamp(UPDATED_COLUMN)
                    .toLocalDateTime());
        } catch (SQLException e) {
            // Ignore
        }
    }

}
