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

import javax.ws.rs.core.Response;

/**
 * <p>Indicates that an internal exception other than an expected one
 * has occurred.  The internal exception is optionally included so
 * that a stack trace can be delivered or logged.</p>
 */
public class InternalServerError extends AbstractException {

    public InternalServerError(String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    public InternalServerError(Throwable cause) {
        super(cause.getMessage(), cause, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
