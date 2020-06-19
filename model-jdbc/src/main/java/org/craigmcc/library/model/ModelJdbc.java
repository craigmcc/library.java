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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>Base interface for Jdbc modules that interact with JDBC
 * representations of {@link Model} classes.  Implementations of
 * these methods will be expected by service class implementations
 * for the corresponding {@link Model} classes.</p>
 *
 * @param <M> The {@link Model} class for which this module defines APIs
 */
public interface ModelJdbc<M extends Model> extends Jdbc {

    /**
     * <p>Create a <code>PreparedStatement</code> that will cause the
     * specified {@link Model} object to be deleted.</p>
     *
     * @param connection <code>Connection</code> to provide the statement
     * @param id Primary key of the {@link Model} object to be deleted
     *
     * @return <code>PreparedStatement</code> ready to be executed
     *
     * @throws SQLException if a JDBC error occurs
     */
    @NotNull PreparedStatement delete(@NotNull Connection connection,
                                      @NotNull Long id)
            throws SQLException;

    /**
     * <p>Create a <code>PreparedStatement</code> that will retrieve
     * all {@link Model} objects from the table for this model type.
     * The resulting <code>ResultSet</code> can be iterated to retrieve
     * all of the matched objects.</p>
     *
     * @param connection <code>Connection</code> to provide the statement
     *
     * @return <code>PreparedStatement</code> ready to be executed
     *
     * @throws SQLException if a JDBC error occurs
     */
    @NotNull PreparedStatement findAll(@NotNull Connection connection)
            throws SQLException;

    /**
     * <p>Create a <code>PreparedStatement</code> that will retrieve
     * the model object with the specified primary key.</p>
     *
     * @param connection <code>Connection</code> to provide the statement
     * @param id Primary key for the {@link Model} object to be retrieved
     *
     * @return <code>PreparedStatement</code> ready to be executed
     *
     * @throws SQLException if a JDBC error occurs
     */
    @NotNull PreparedStatement findById(@NotNull Connection connection,
                                        @NotNull Long id)
            throws SQLException;

    /**
     * <p>Create a <code>PreparedStatement</code> that will insert
     * a new model object into the underlying table.  The first
     * object in the first row of the resulting <code>ResultSet</code>
     * will contain the primary key of the newly created model object.</p>
     *
     * @param connection <code>Connection</code> to provide the statement
     * @param model {@link Model} object to be inserted
     *
     * @return <code>PreparedStatement</code> ready to be executed
     *
     * @throws SQLException if a JDBC error occurs
     */
    @NotNull PreparedStatement insert(@NotNull Connection connection,
                                      @NotNull M model)
            throws SQLException;

    /**
     * <p>Copy values from the each row of the specified
     * <code>ResultSet</code> into a newly created {@link Model}
     * object for each row, ignoring any fields not present in the results
     * returned from the database (because a SELECT that only
     * returned specific columns might have been executed.</p>
     *
     * <p><strong>WARNING: </strong> Calling this method will have
     * caused the underlying <code>ResultSet</code> to be positioned
     * to the last row when completed.</p>
     *
     * @param resultSet <code>ResultSet</code> from which to acquire
     *                  returned column values
     *
     * @return A newly created {@link Model} object that is populated
     *         with whatever column values were present in the specified
     *         <code>ResultSet</code> (if there are no matching rows in the
     *         <code>ResultSet</code>, an empty <code>List</code> will
     *         be returned)
     *
     * @throws SQLException if a JDBC error occurs
     */
    @NotNull List<M> populateAll(@NotNull ResultSet resultSet)
            throws SQLException;

    /**
     * <p>Copy values from the next row of the specified
     * <code>ResultSet</code> into a newly created {@link Model}
     * object, ignoring any fields not present in the results
     * returned from the database (because a SELECT that only
     * returned specific columns might have been executed.</p>
     *
     * <p><strong>WARNING: </strong> Calling this method will have
     * caused the underlying <code>ResultSet</code> to be positioned
     * to the next (usually only) row.</p>
     *
     * @param resultSet <code>ResultSet</code> from which to acquire
     *                  returned column values
     *
     * @return A newly created {@link Model} object that is populated
     *         with whatever column values were present in the specified
     *         <code>ResultSet</code>, or <code>null</code> if there is
     *         no "next" row in the <code>ResultSet</code>
     *
     * @throws SQLException if a JDBC error occurs
     */
    M populateNext(@NotNull ResultSet resultSet)
            throws SQLException;

    /**
     * <p>Create a <code>PreparedStatement</code> that will update
     * an existing model object in the underlying table.</p>
     *
     * @param connection <code>Connection</code> to provide the statement
     * @param model {@link Model} object to be updated
     *
     * @return <code>PreparedStatement</code> ready to be executed
     *
     * @throws SQLException if a JDBC error occurs
     */
    @NotNull PreparedStatement update(@NotNull Connection connection,
                                      @NotNull M model)
            throws SQLException;

}
