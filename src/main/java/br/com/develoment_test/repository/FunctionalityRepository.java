package br.com.develoment_test.repository;

import br.com.develoment_test.domain.Functionality;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Functionality entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FunctionalityRepository extends JpaRepository<Functionality, Long> {

    public Functionality getByNameAndAuthority_Name(String name, String authority_name);

}
