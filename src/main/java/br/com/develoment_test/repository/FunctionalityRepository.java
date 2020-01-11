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


    public Functionality getByName(String name);

//    @Query("SELECT f FROM Functionality as f WHERE name = ?1 and authority_name = ?2")
//    public Functionality getFunctionalityByNameAndAuthority_Name(String name, String authority);

    public Functionality getByNameAndAuthority_Name(String name, String authority_name);
}
