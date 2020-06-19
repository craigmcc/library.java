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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.craigmcc.library.model.Constants.ID_COLUMN;
import static org.craigmcc.library.model.Constants.PUBLISHED_COLUMN;
import static org.craigmcc.library.model.Constants.UPDATED_COLUMN;

/**
 * <p>Abstract base class for builders that generate SQL statements.  Note that
 * many of the methods apply only to certain SQL statement types (DELETE, INSERT,
 * SELECT, UPDATE).  Check the documentation for each method to see whether it is
 * relevant or not for the builder you are creating.</p>
 *
 * @param <B> The builder class that is subclassing this base class
 */
public abstract class AbstractStatementBuilder<B extends StatementBuilder>
        implements StatementBuilder {

    // Instance Variables ----------------------------------------------------

    protected boolean all = false;
    protected final List<Clause> clauses = new ArrayList<>();
    protected final List<Expression> expressions = new ArrayList<>();
    protected boolean distinct = false;
    protected final List<String> groupBys = new ArrayList<>();
    protected Integer limit;
    protected Integer offset;
    protected boolean or = false;
    protected final List<OrderBy> orderBys = new ArrayList<>();
    protected final List<Pair> pairs = new ArrayList<>();
    protected final List<Object> params = new ArrayList<>();
    protected Pair primary = null;
    protected String sql = null; // Only useful for debugging via toString() calls after the fact
    protected final List<String> tables = new ArrayList<>();

    // Constructors ----------------------------------------------------------

    // TODO - support multiple tables for joins?  With abbreviations for columns?
    public AbstractStatementBuilder(@NotNull String table) {
        tables.add(table);
    }

    // Public Methods --------------------------------------------------------

    /**
     * <p><strong>RELEVANT ON:</strong> DELETE, SELECT, UPDATE</p>
     *
     * <p>Indicate that we really do want this statement to affect all rows in the table
     * (otherwise this will generate an IllegalStateException).</p>
     */
    public B all() {
        all = true;
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> DELETE, SELECT, UPDATE.</p>
     *
     * <p>Store the columns and operator for a condition that must be
     * matched in order to match a row.  Such conditions are ignored
     * if <code>primary()</code> has been called.</p>
     *
     * @param leftColumn Column to the left of the operator
     * @param operator The operator used to compare the columns
     * @param rightColumn Column to the right of the operator
     *
     * @return This builder
     */
    public B clause(@NotNull String leftColumn, @NotNull SqlOperator operator, @NotNull String rightColumn) {
        clauses.add(new Clause(leftColumn, operator, rightColumn));
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> DELETE, SELECT, UPDATE.</p>
     *
     * <p>Store the column, operator, and a literal expression value
     * for a condition that must be satisfied in order to match.</p>
     *
     * @param column Column to be matched
     * @param operator The operator used to compare column and value
     * @param value Literal expression value to be matched
     *
     * @return This builder
     */
    public B expression(@NotNull String column, @NotNull SqlOperator operator, @NotNull Object value) {
        expressions.add(new Expression(column, operator, value));
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> DELETE, SELECT, UPDATE</p>
     *
     * <p>Cause matching conditions specified by <code>clause()</code>
     * or <code>expression()</code> to be linked by "OR" instead of "AND".</p>
     *
     * @return This builder
     */
    public B or() {
        this.or = true;
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> INSERT, UPDATE.</p>
     *
     * <p>Store a paired reference between an SQL column name and a corresponding
     * value to replace it with.  Column names and values will be generated in the
     * order that <code>pair()</code> is called as the builder is assembled.</p>
     *
     * <p><strong>NOTE:</strong> Paired references are only useful on INSERT and
     * UPDATE statements.</p>
     *
     * @param column Name of the SQL column to be included
     * @param value Corresponding value to be included (will be enclosed in quotes if a String)
     *
     * @return This builder
     */
    public B pair(@NotNull String column, Object value) {
        pairs.add(new Pair(column, value));
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> INSERT, UPDATE.</p>
     *
     * <p>Record a pairing only if the specified value is not <code>null</code>.</p>
     *
     * @param column Name of the SQL column to be included
     * @param value Corresponding value to be included (will be enclosed in quotes if a String)
     *
     * @return This builder
     */
    public B pairIfNotNull(@NotNull String column, @NotNull Object value) {
        if (value != null) {
            pair(column, value);
        }
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> INSERT, UPDATE.</p>
     *
     * <p>Record a pairing for a literal value (such as an SQL function call) that will
     * not be enclosed in quotes in the generated statement.</p>
     *
     * @param column Name of the SQL column to be included
     * @param value The literal value to be included
     *
     * @return This builder
     */
    public B pairLiteral(@NotNull String column, @NotNull Object value) {
        pairs.add(new Pair(column, true, value));
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> INSERT, UPDATE.</p>
     *
     * <p>Pair the columns and values from those included via the {@link Model}
     * base class in every such object.  The primary key (if any) will be stored
     * separately, via a call to <code>primary()</code>, so that it can be used
     * in the WHERE clause of a DELETE or UPDATE statement.</p>
     *
     * @param model The model object from which to copy common columns and values
     *
     * @return This builder
     */
    public B pairModel(@NotNull Model model) {
        primary(ID_COLUMN, model.getId());
        // TODO - verify LocalDateTime conversions are supported
        pair(PUBLISHED_COLUMN, model.getPublished());
        pair(UPDATED_COLUMN, model.getUpdated());
        return (B) this;
    }

    /**
     * <p><strong>RELEVANT ON:</strong> DELETE, SELECT, UPDATE.</p>
     *
     * <p>Store the specified name and value of the primary key for this object
     * so that it can be used in the WHERE clause of a DELETE, SELECT, or UPDATE
     * statement.  If specified on DELETE, SELECT, or UPDATE statements, any
     * clauses specified by <code>clause()</code> and related methods will
     * be ignored.</p>
     *
     * <p><strong>NOTE:</strong> If you are dealing with a {@link Model} object,
     * calling <code>pairModel()</code> will have done this for you already.</p>
     *
     * @param column Name of the primary key column for this table
     * @param value Primary key value for this object
     *
     * @return This builder
     */
    public B primary(@NotNull String column, Object value) {
        this.primary = new Pair(column, true, value);
        return (B) this;
    }

    /**
     * <p>Return the string representation of the SQL statement generated by
     * this builder.  Only includes the SQL text after <code>build()</code> has
     * been called.  Useful only for debugging the builders themselves.</p>
     *
     * @return Values of all the internally stored builder information
     */
    public String toString() {
        return this.getClass().getSimpleName() +
                "{all=" + all + ", clauses=" + clauses + ", distinct=" + distinct +
                ", expressions=" + expressions +
                ", groupBys=" + groupBys + ", limit=" + limit + ", offset=" + offset +
                ", or=" + or + ", orderBys=" + orderBys + ",pairs=" + pairs +
                ", params=" + params + ", primary=" + primary + ", sql=" + sql + "}";
    }

    // Protected Methods ---------------------------------------------------------

    /**
     * <p>Add a WHERE clause to the SQL text being created.  This will either
     * be a direct primary key match (if <code>primary()</code> was specified)
     * or one or more conditions (if <code>clause()</code> or <code>expression()</code>
     * was specified, possibly more than once).  In the latter case, if
     * <code>or()</code> was specified, the conditions will be linked by OR;
     * otherwise they will be linked by AND.</p>
     *
     * <p>If neither a primary key nor at least one condition was specified,
     * throw an exception to avoid generating statements that will impact every
     * row in the underlying table.</p>
     *
     * @param sb StringBuilder containing the SQL text being created
     */
    protected void addWhere(StringBuilder sb) throws IllegalStateException {

        // Allow no WHERE conditions only if it was explicitly requested
        if (all) {
            return;
        }
        // Otherwise, some condition(s) must have been explicitly requested
        if ((primary == null) && (clauses.size() == 0) && (expressions.size() == 0)) {
            throw new IllegalStateException("Must specify either a primary key or conditions");
        }

        if (primary != null) {

            // Match on equality to primary key value
            sb.append(" WHERE (");
            sb.append(primary.column);
            sb.append(" = ");
            if (primary.literal) {
                sb.append(primary.value);
            } else {
                sb.append("?");
                params.add(primary.value);
            }
            sb.append(")");

        } else {

            // Match on specified condition(s)
            sb.append(" WHERE ");
            boolean first = true;

            // First, do clauses (comparison between two column values)
            for (Clause clause : clauses) {
                if (first) {
                    first = false;
                    sb.append("(");
                } else {
                    if (or) {
                        sb.append(" OR (");
                    } else {
                        sb.append(" AND (");
                    }
                }
                sb.append(clause.leftColumn);
                sb.append(" ");
                sb.append(clause.operator.getOperator());
                sb.append(" ");
                sb.append(clause.rightColumn);
                sb.append(")");
            }

            // Second, do expressions (comparison between a column value and a literal)
            for (Expression expression : expressions) {
                if (first) {
                    first = false;
                    sb.append("(");
                } else {
                    if (or) {
                        sb.append(" OR (");
                    } else {
                        sb.append(" AND (");
                    }
                }
                sb.append(expression.column);
                sb.append(" ");
                sb.append(expression.operator.getOperator());
                sb.append(" ");
                sb.append(expression.expression);
                sb.append(")");
            }

        }

    }

    /**
     * <p>Add a parameter value that will be used as a replacement value in the
     * generated SQL statement, if required.  These values <strong>MUST</strong>
     * be recorded in the order of their replacement indicators ("?")
     * in the generated statement text, because they are replaced
     * in the order of calls to this method.</p>
     *
     * @param param The replacement value to be recorded
     *
     * @return This builder
     */
    protected B addParam(Object param) {
        params.add(param);
        return (B) this;
    }

    /**
     * <p>Apply any specified parameters to the <code>PreparedStatement</code>
     * that is being generated.</p>
     *
     * @param statement The <code>PreparedStatement</code> to update
     *
     * @throws SQLException if a SQL exception occurs
     */
    protected void applyParams(PreparedStatement statement) throws SQLException {
        if (params.size() > 0) {
            if (statement != null) { // TODO - Mockito does not generate this :-(
                for (int i = 0; i < params.size(); i++) {
                    statement.setObject(i + 1, params.get(i));
                }
            }
        }
    }

    // Support classes ---------------------------------------------------------

    /**
     * <p>A first column name, SQL operator, and second column name denoting
     * two columns that will be compared (according to the specified operator
     * in a WHERE clause.</p>
     */
    protected static class Clause {

        Clause(@NotNull String leftColumn, @NotNull SqlOperator operator, @NotNull String rightColumn) {
            this.leftColumn = leftColumn;
            this.operator = operator;
            this.rightColumn = rightColumn;
        }

        final String leftColumn;
        final SqlOperator operator;
        final Object rightColumn;

        @Override
        public String toString() {
            return "Clause{" +
                    "leftColumn=" + leftColumn +
                    ", operator=" + operator +
                    ", rightColumn=" + rightColumn + "}";
        }

    }

    /**
     * <p>A column name, SQL operator, and literal value to which that column
     * will be compared in a WHERE clause.</p>
     */
    protected static class Expression {

        Expression(@NotNull String column, @NotNull SqlOperator operator, @NotNull Object expression) {
            this.column = column;
            this.operator = operator;
            this.expression = expression;
        }

        final String column;
        final Object expression;
        final SqlOperator operator;

        @Override
        public String toString() {
            return "Clause{" +
                    "column=" + column +
                    ", operator=" + operator +
                    ", expression=" + expression + "}";
        }

    }

    /**
     * <p>A column name and direction to be included in an ORDER BY clause.</p>
     */
    protected static class OrderBy {

        OrderBy(@NotNull String column, @NotNull SqlDirection direction) {
            this.column = column;
            this.direction = direction;
        }

        final String column;
        final SqlDirection direction;

        @Override
        public String toString() {
            return "OrderBy{" +
                    "column=" + column + '\'' +
                    ", direction='" + direction + "'}";

        }

    }

    /**
     * <p>A column name, and a value to be stored on an INSERT or UPDATE,
     * with the value being either a literal expression (literal == true)
     * or an object that will be the value inserted or updated.</p>
     */
    protected class Pair {
        
        Pair(@NotNull String column, Object value) {
            this.column = column;
            this.literal = false;
            this.value = value;
        }
        
        Pair(@NotNull String column, boolean literal, Object value) {
            this.column = column;
            this.literal = literal;
            this.value = value;
        }

        final String column;
        final boolean literal;
        final Object value;

        @Override
        public String toString() {
            return "Pair{" +
                    "column=" + column +
                    ", literal=" + literal +
                    ", value=" + value + "}";
                    
        }
    
    }

}
