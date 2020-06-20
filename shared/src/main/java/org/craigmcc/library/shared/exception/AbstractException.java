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
package org.craigmcc.library.shared.exception;

/**
 * <p>Abstract base class for common exceptions that describe application
 * tier issues, rather than internal execution issues.  They include a
 * place to store the corresponding HTTP status code (accessible via
 * <code>getStatusCode()</code> that should be returned by HTTP endpoints.</p>
 */
public class AbstractException extends Exception {

    // Instance Variables ----------------------------------------------------

    protected int statusCode;

    // Constructors ----------------------------------------------------------

    public AbstractException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AbstractException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public AbstractException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    // Public Methods --------------------------------------------------------

    public int getStatusCode() {
        return this.statusCode;
    }

}
