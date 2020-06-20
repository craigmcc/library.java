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
 * <p>Indicates that a validation error occurred processing a request.</p>
 */
public class BadRequest extends AbstractException {

    public BadRequest(String message) {
        super(message, Response.Status.BAD_REQUEST.getStatusCode());
    }

}
