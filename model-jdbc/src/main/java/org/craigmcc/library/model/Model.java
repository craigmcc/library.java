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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>Abstract base class for model objects.</p>
 */
public abstract class Model<M> implements Cloneable, Constants, Serializable {

    // Instance Variables ----------------------------------------------------

    @Schema(description = "Primary key for this model object.")
    private Long id;

    @Schema(description = "Date and time this model object was initially created.")
    private LocalDateTime published;

    @Schema(description = "Date and time this model object was most recently updated.")
    private LocalDateTime updated;

    // Static Variables ------------------------------------------------------

    // Property Methods ------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

// Public Methods --------------------------------------------------------

    @Override
    public M clone() {
        try {
            return (M) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * <p>Copy user-modifiable properties from the <code>from</code>
     * object into the current object.  This <strong>MUST</strong>
     * be implemented by all concrete Model classes.</p>
     *
     * @param from Object to copy from into this object
     */
    public abstract void copy(M from);

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Model)) {
            return false;
        }
        Model that = (Model) object;
        if (!this.getClass().equals(that.getClass())) {
            return false;
        }
        return new EqualsBuilder()
                .append(this.id, that.id)
                // published/updated are deliberately omitted
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                // published/updated are deliberately omitted
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(ID_COLUMN, this.id)
                .append(PUBLISHED_COLUMN, this.published)
                .append(UPDATED_COLUMN, this.updated)
                .toString();
    }

}
