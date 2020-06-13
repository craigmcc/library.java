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

import org.craigmcc.library.model.Constants;
import org.craigmcc.library.model.Model;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.craigmcc.library.model.Constants.ID_COLUMN;
import static org.craigmcc.library.model.Constants.PUBLISHED_COLUMN;
import static org.craigmcc.library.model.Constants.UPDATED_COLUMN;

public abstract class AbstractStatementBuilder<B extends StatementBuilder> implements StatementBuilder {

    // Instance Variables ----------------------------------------------------

    protected final List<String> groupBys = new ArrayList<>();
    protected final List<OrderBy> orderBys = new ArrayList<>();
    protected final List<Pair> pairs = new ArrayList<>();
    protected final List<Object> params = new ArrayList<>();
    protected Pair primary = null;
    protected String sql = null; // Only useful for debugging via toString() calls after the fact
    protected final List<String> tables = new ArrayList<>();

    // Constructors ----------------------------------------------------------

    public AbstractStatementBuilder(@NotNull String table) { // TODO - support multiples
        tables.add(table);
    }

    // Public Methods --------------------------------------------------------

    /**
     * <p>Store the name of the column by which results of a SELECT statement
     * should be grouped.  These will be applied to a SELECT statement in the
     * order that they were added.</p>
     *
     * @param column Column name on which to group
     *
     * @return This builder
     */
    public B groupBy(@NotNull String column) {
        groupBys.add(column);
        return (B) this;
    }

    /**
     * <p>Store an <code>OrderBy</code> representing the specified column name
     * and direction.  These will be applied to a SELECT statement in the order
     * that they were added.</p>
     *
     * @param column Column name on which to sort
     * @param direction Direction (ascending or descending) for this sort
     *
     * @return This builder
     */
    public B orderBy(@NotNull String column, @NotNull SqlDirection direction) {
        orderBys.add(new OrderBy(column, direction));
        return (B) this;
    }

    /**
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
/*
        if (value instanceof Iterable) {
            value = Joiner.on(",").skipNulls().join((Iterable<?>) value);
        } else if (value instanceof Model) {
            value = ((Model) value).getId();
        }
*/
        pairs.add(new Pair(column, value));
        return (B) this;
    }

    /**
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
     * <p>Record a pairing for a literal value (such as an SQL function call) that will
     * not be enclosed in quotes in the generated statement.</p>
     *
     * @param column Name of the SQL column to be included
     * @param value The literal value to be included
     *
     * @return This builder
     */
    public B pairLiteral(@NotNull String column, @NotNull String value) {
        pairs.add(new Pair(column, true, value));
        return (B) this;
    }

    /**
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
        pair(PUBLISHED_COLUMN, model.getPublished());
        pair(UPDATED_COLUMN, model.getUpdated());
        return (B) this;
    }

    /**
     * <p>Add a parameter value that will be used as a replacement value in the
     * generated SQL statement, if required.  These values <strong>MUST</strong>
     * be recorded in the order of their replacement indicators (such as "?1")
     * in the statement text, because they are replaced in numerical sequence.</p>
     *
     * @param param The replacement value to be recorded
     *
     * @return This builder
     */
    public B param(Object param) {
        params.add(param);
        return (B) this;
    }

    /**
     * <p>Store the specified name of the primary key column for this object
     * so that it can be retrieved after an INSERT statement is completed.
     *
     * <p><strong>NOTE:</strong> If you are dealing with a {@link Model} object,
     * calling <code>pairModel()</code> will have done this for you already.</p>
     *
     * @param column Name of the primary key column for this table
     *
     * @return This builder
     */
    public B primary(@NotNull String column) {
        this.primary = new Pair(column, null);
        return (B) this;
    }

    /**
     * <p>Store the specified name and value of the primary key for this object
     * so that it can be used in the WHERE clause of a DELETE or UPDATE statement.</p>
     *
     * <p><strong>NOTE:</strong> If you are dealing with a {@link Model} object,
     * calling <code>pairModel()</code> will have done this for you already.</p>
     *
     * <p><strong>NOTE:</strong> For an INSERT statement, the primary key reference
     * is only used to remember the name of the primary key column that will be
     * generated (any specified value will be ignored).  For a DELETE or UPDATE
     * statement, it will be used as part of the generated WHERE clause.</p>
     *
     * @param column Name of the primary key column for this table
     * @param value Primary key value for this object (ignored for an INSERT statement)
     *
     * @return This builder
     */
    public B primary(@NotNull String column, Object value) {
        this.primary = new Pair(column, value);
        return (B) this;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[groupBys=" + groupBys + ", orderBys=" + orderBys + ",pairs=" +
                pairs + ", params=" + params + ", primary=" + primary + ", sql=" + sql + "]";
    }

    // Protected Methods ---------------------------------------------------------

    protected void apply(PreparedStatement statement) throws SQLException {
        if (params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
        }
    }

    // Support classes ---------------------------------------------------------
    
/*
    // Simplified from com.google.base.Joiner
    protected static class Joiner {

        // Instance Variables ------------------------------------------------

        private String forNull = "null";
        private final String separator;
        private boolean skipNulls = false;

        // Constructors ------------------------------------------------------

        protected Joiner(char separator) {
            this.separator = String.valueOf(separator);
        }

        protected Joiner(@NotNull String separator) {
            this.separator = separator;
        }

        // Static Methods ----------------------------------------------------

        protected static Joiner on(char separator) {
            return new Joiner(String.valueOf(separator));
        }

        protected static Joiner on(@NotNull String separator) {
            return new Joiner(separator);
        }

        // Builder Methods ---------------------------------------------------

        protected Joiner forNull(String forNull) {
            this.forNull = forNull;
            return this;
        }

        protected Joiner skipNulls() {
            this.skipNulls = true;
            return this;
        }

        // Protected Methods -------------------------------------------------

        protected final String join(Iterable<?> parts) {
            StringBuilder sb = new StringBuilder();
            for (Object part : parts) {
                if (!skipNulls || (part != null)) {
                    if (sb.length() > 0) {
                        sb.append(separator);
                    }
                    if (part == null) {
                        sb.append(forNull);
                    } else {
                        sb.append(part.toString());
                    }
                }
            }
            return sb.toString();
        }


    }
*/

    private static class OrderBy {

        OrderBy(@NotNull String column, @NotNull SqlDirection direction) {
            this.column = column;
            this.direction = direction;
        }

        final String column;
        final SqlDirection direction;

    }

    static class Pair {
        
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
                    "column=" + column + '\'' +
                    ", value='" + value + "'}";
                    
        }
    
    }

}
