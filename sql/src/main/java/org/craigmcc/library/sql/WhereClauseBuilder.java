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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>UNUSED but leftover from previous iterations of this package.</p>
 */
public class WhereClauseBuilder {
    
    public static final char ESCAPER = '\\';
    public static final char WILDCARD = '%';

    private boolean or = false;
    private final List<Object> params = new ArrayList<>();
    private final List<Where> wheres = new ArrayList<>();
    
    // Public methods ----------------------------------------------------------
    
    // " WHERE {clause}" or ""
    @NotNull
    public String build() {
        String substatement = buildSubclause();
        return substatement.isEmpty() ? substatement : " WHERE" + substatement;
    }

    public WhereClauseBuilder or() {
        return or(true);
    }
    
    public WhereClauseBuilder or(boolean or) {
        this.or = or;
        return this;
    }
    
    // Only useful after build() has been called
    public List<Object> params() {
        return this.params;
    }
    
    @Override
    public String toString() {
        return build();
    }
    
    /**
     * Add an expression that will be the output of the whereClauseBuilder argument.
     * If expression is null or has no statements, this is a noop.
     * 
     * @param expression Expression to be added
     */
    public WhereClauseBuilder where(WhereClauseBuilder expression) {
        if ((expression == null) || expression.hasContent()) {
            return this;
        }
        wheres.add(new SubWhere(expression));
        return this;
    }
    
    /**
     * Add an expression that will filter results based on the specified parameters,
     * which can include an IS NULL or IS NOT NULL comparison.
     * 
     * @param column Name of the column to be filtered on
     * @param value Value to be compared
     */
    public WhereClauseBuilder where(@NotNull String column, Object value) {
        boolean wildcarded = false;
        if (value instanceof String) {
            wildcarded = ((String) value).indexOf(WILDCARD) >= 0;
        }
        if (wildcarded) {
            return where(column, SqlOperator.LIKE, value);
        } else {
            return where(column, SqlOperator.EQ, value);
        }
    }

    /**
     * Add an two expressions that will filter results based on the specified parameters,
     * which can include IS NULL and IS NOT NULL comparisons.
     * 
     * @param column1 Name of the first column to be filtered on
     * @param value1 Value to be compared to the first column
     * @param column2 Name of the second column to be filtered on
     * @param value2 Value to be compared to the second column
     */
    public WhereClauseBuilder where(@NotNull String column1, Object value1,
                                    @NotNull String column2, Object value2) {
        SqlOperator operator1 = SqlOperator.EQ;
        if ((value1 instanceof String) && ((String) value1).indexOf(WILDCARD) >= 0) {
            operator1 = SqlOperator.LIKE;
        }
        SqlOperator operator2 = SqlOperator.EQ;
        if ((value2 instanceof String) && ((String) value2).indexOf(WILDCARD) >= 0) {
            operator2 = SqlOperator.LIKE;
        }
        return where(column1, operator1, value1, column2, operator2, value2);
    }
    
    /**
     * Add an expression for comparing the specified column name to the specified
     * value with the specified operator.
     * 
     * @param column Name of the column to be filtered on
     * @param operator Operator for the comparison
     * @param value Value to be compared
     */
    public WhereClauseBuilder where(@NotNull String column, @NotNull SqlOperator operator, Object value) {
        if (column == null) {
            throw new IllegalArgumentException("WHERE column cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("WHERE operator cannot be null");
        }
        wheres.add(new Where(column, operator, value));
        return this;
    }

    /**
     * Add an expression for comparing the specified column namea to the specified
     * valuea with the specified operatora.
     * 
     * @param column1 Name of the first column to be filtered on
     * @param operator1 Operator for the first comparison
     * @param value1 First value to be compared
     * @param column2 Name of the second column to be filtered on
     * @param operator2 Operator for the second comparison
     * @param value2 Second value to be compared
     */
    public WhereClauseBuilder where(@NotNull String column1, @NotNull SqlOperator operator1, Object value1,
                                    @NotNull String column2, @NotNull SqlOperator operator2, Object value2) {
        if ((column1 == null) || (column2 == null)) {
            throw new IllegalArgumentException("WHERE column cannot be null");
        }
        if ((operator1 == null) || (operator2 == null)) {
            throw new IllegalArgumentException("WHERE operator cannot be null");
        }
        wheres.add(new Where(column1, operator1, value1, column2, operator2, value2));
        return this;
    }
    
    // Support methods ---------------------------------------------------------
    
    // " {clause}" or ""
    @NotNull
    private String buildSubclause() {
        
        // Avoid issue if build is called twice
        params.clear();
        
        if (!hasContent()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Where where : wheres) {
            if (first) {
                sb.append(" ");
            } else {
                sb.append(this.or ? " OR " : " AND ");
            }
            first = false;
            sb.append("(");
            if (where instanceof SubWhere) {
                sb.append(where.expression);
                params.addAll(((SubWhere) where).params);
            } else if (where.expression != null) {
                sb.append(where.expression);
            } else if (where.column2 != null) {
                sb.append("(");
                sb.append(expression(where.column1, where.operator1, where.value1));
                sb.append(") OR (");
                sb.append(expression(where.column2, where.operator2, where.value2));
                sb.append(")");
            } else {
                sb.append(expression(where.column1, where.operator1, where.value1));
            }
            sb.append(")");
        }
        return sb.toString();
    
    }
    
    private String expression(String column, SqlOperator operator, Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append(column);
        sb.append(" ");
        sb.append(operator.getOperator());
        sb.append(" ");
        if (value != null) {
            sb.append(operator.getOperator());
            sb.append(" ");
            if (Utils.literal(value)) {
                sb.append(value);
            } else {
                sb.append("?");
                params.add(value);
            }
        } else {
            switch (operator) {
                case EQ:
                    sb.append("IS NULL");
                    break;
                case NE:
                    sb.append("IS NOT NULL");
                    break;
                default:
                    throw new IllegalArgumentException("Cannot use operator '" + operator.name() + "' against a null value");
            }
        }
        return sb.toString();
    }
    
    /**
     * Check if the whereClause has any content.
     */
    private boolean hasContent() {
        return !wheres.isEmpty();
    }
    
    // Inner classes -----------------------------------------------------------
    
    static class Where {
        
        Where(@NotNull String expression) {
            this.expression = expression;
            this.column1 = null;
            this.operator1 = null;
            this.value1 = null;
            this.column2 = null;
            this.operator2 = null;
            this.value2 = null;
        }

        Where(@NotNull String column1, @NotNull SqlOperator operator1, Object value1) {
            this.expression = null;
            this.column1 = column1;
            this.operator1 = operator1;
            this.value1 = value1;
            this.column2 = null;
            this.operator2 = null;
            this.value2 = null;
        }
        
        Where(@NotNull String column1, @NotNull SqlOperator operator1, Object value1,
              @NotNull String column2, @NotNull SqlOperator operator2, Object value2) {
            this.expression = null;
            this.column1 = column1;
            this.operator1 = operator1;
            this.value1 = value1;
            this.column2 = column2;
            this.operator2 = operator2;
            this.value2 = value2;
        }
        
        final String expression;
        final String column1;
        final SqlOperator operator1;
        final Object value1;
        final String column2;
        final SqlOperator operator2;
        final Object value2;
    
    }

    public class SubWhere extends Where {

        private final List<Object> params;
        
        public SubWhere(WhereClauseBuilder whereClauseBuilder) {
            super(whereClauseBuilder.buildSubclause());
            params = whereClauseBuilder.params();
        }
    
    }

}
