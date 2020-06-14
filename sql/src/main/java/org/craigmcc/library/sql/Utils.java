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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * <p>UNUSED but leftover from previous iterations of this package.</p>
 */
class Utils {
    
    private static Set<String> literals =
            Set.of("now()", "now(3)");

    public static void apply(PreparedStatement stmt, List<Object> params) throws SQLException {
        if ((params != null) && (params.size() > 0)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
        }
    }

    public static void apply(PreparedStatement stmt, Object... params) throws SQLException {
        apply(stmt, Arrays.asList(params));
    }

    public static boolean literal(Object value) {
        if (literals.contains(value)) {
            return true;
        }
        return false;
    }

}
