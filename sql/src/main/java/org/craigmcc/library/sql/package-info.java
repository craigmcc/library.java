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

/**
 * <p>The concrete classes in this package are convenience builders
 * that accept the table name of the table to be affected by the
 * generated SQL statement, as well as various optional decorator
 * methods that will customize the nature of the generated statement.
 * When you call <code>build()</code>, you will get back a
 * JDBC <code>PreparedStatement</code>, formulated from a syntactically
 * correct SQL statement (based on the specified table name and
 * optional decorators), with any needed parameter values (for "?"
 * replacement) already applied.  This prepared statement is ready
 * to be executed and then closed by the calling logic.</p>
 *
 * <p>For good examples of the variety of decorators that can be
 * used, and the precise syntax of the SQL statement that is generated,
 * see the various unit tests for these builder classes.</p>
 */
package org.craigmcc.library.sql;
