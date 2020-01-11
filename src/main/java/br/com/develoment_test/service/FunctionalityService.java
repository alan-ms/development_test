package br.com.develoment_test.service;

import br.com.develoment_test.domain.Functionality;
import br.com.develoment_test.repository.FunctionalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Functionality}.
 */
@Service
@Transactional
public class FunctionalityService {

    private final Logger log = LoggerFactory.getLogger(FunctionalityService.class);

    private final FunctionalityRepository functionalityRepository;

    public FunctionalityService(FunctionalityRepository functionalityRepository) {
        this.functionalityRepository = functionalityRepository;
    }

    /**
     * Save a functionality.graphqls.
     *
     * @param functionality the entity to save.
     * @return the persisted entity.
     */
    public Functionality save(Functionality functionality) {
        return functionalityRepository.save(functionality);
    }

    /**
     * Get all the functionalities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Functionality> findAll() {
        log.debug("Request to get all Functionalities");
        return functionalityRepository.findAll();
    }


    /**
     * Get one functionality.graphqls by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Functionality> findOne(Long id) {
        log.debug("Request to get Functionality : {}", id);
        return functionalityRepository.findById(id);
    }

    /**
     * Delete the functionality.graphqls by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Functionality : {}", id);
        functionalityRepository.deleteById(id);
    }
}
