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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

/**
 * <p>Abstract base class for model objects.</p>
 */
@Entity
@Inheritance(strategy = TABLE_PER_CLASS)
@Access(AccessType.FIELD)
public abstract class Model<M> implements Cloneable, Constants, Serializable {

    // Instance Variables ----------------------------------------------------

    @Column(
            name = ID_COLUMN,
            nullable = false,
            unique = true
    )
    @GeneratedValue
    @Id
    @Schema(description = "Primary key for this model object.")
    private Long id;

    @Column(
            columnDefinition = "TIMESTAMP",
            name = PUBLISHED_COLUMN,
            nullable = false
    )
    @Schema(description = "Date and time this model object was initially created.")
    private LocalDateTime published;

    @Column(
            columnDefinition = "TIMESTAMP",
            name = UPDATED_COLUMN,
            nullable = false
    )
    @Schema(description = "Date and time this model object was most recently updated.")
    private LocalDateTime updated;

    @Column(
            name = VERSION_COLUMN
    )
    @Version
    @Schema(description = "Entity version for optimistic locking.")
    private Integer version;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
     * @param from
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
                // published/updated/version are deliberately omitted
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                // published/updated/version are deliberately omitted
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(ID_COLUMN, this.id)
                .append(PUBLISHED_COLUMN, this.published)
                .append(UPDATED_COLUMN, this.updated)
                .append(VERSION_COLUMN, this.version)
                .toString();
    }

}
