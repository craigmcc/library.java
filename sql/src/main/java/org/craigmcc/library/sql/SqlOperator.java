/*
 * Copyright20 craigmcc.
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

public enum SqlOperator {
    
    LT("<"),
    GT(">"),
    LE("<="),
    GE(">="),
    EQ("="),
    NE("<>"),
    LIKE("LIKE");

    SqlOperator(String operator) {
        this.operator = operator;
    }

    @NotNull
    private String operator;

    public String getOperator() {
        return this.operator;
    }

}
