package br.com.develoment_test.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A Functionality.
 */
@Entity
@Table(name = "functionality")
public class Functionality implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("functionalities")
    private Authority authority;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Functionality name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Authority getAuthority() {
        return authority;
    }

    public Functionality authority(Authority authority) {
        this.authority = authority;
        return this;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Functionality)) {
            return false;
        }
        return id != null && id.equals(((Functionality) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Functionality{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
