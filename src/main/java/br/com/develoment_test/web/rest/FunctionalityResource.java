package br.com.develoment_test.web.rest;

import br.com.develoment_test.domain.Functionality;
import br.com.develoment_test.service.FunctionalityService;
import br.com.develoment_test.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link br.com.develoment_test.domain.Functionality}.
 */
@RestController
@RequestMapping("/api")
public class FunctionalityResource {

    private final Logger log = LoggerFactory.getLogger(FunctionalityResource.class);

    private static final String ENTITY_NAME = "functionality";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FunctionalityService functionalityService;

    public FunctionalityResource(FunctionalityService functionalityService) {
        this.functionalityService = functionalityService;
    }

    /**
     * {@code POST  /functionalities} : Create a new functionality.
     *
     * @param functionality the functionality to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new functionality, or with status {@code 400 (Bad Request)} if the functionality has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/functionalities")
    public ResponseEntity<Functionality> createFunctionality(@Valid @RequestBody Functionality functionality) throws URISyntaxException {
        log.debug("REST request to save Functionality : {}", functionality);
        if (functionality.getId() != null) {
            throw new BadRequestAlertException("A new functionality cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Functionality result = functionalityService.save(functionality);
        return ResponseEntity.created(new URI("/api/functionalities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /functionalities} : Updates an existing functionality.
     *
     * @param functionality the functionality to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated functionality,
     * or with status {@code 400 (Bad Request)} if the functionality is not valid,
     * or with status {@code 500 (Internal Server Error)} if the functionality couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/functionalities")
    public ResponseEntity<Functionality> updateFunctionality(@Valid @RequestBody Functionality functionality) throws URISyntaxException {
        log.debug("REST request to update Functionality : {}", functionality);
        if (functionality.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Functionality result = functionalityService.save(functionality);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, functionality.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /functionalities} : get all the functionalities.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of functionalities in body.
     */
    @GetMapping("/functionalities")
    public List<Functionality> getAllFunctionalities() {
        log.debug("REST request to get all Functionalities");
        return functionalityService.findAll();
    }

    /**
     * {@code GET  /functionalities/:id} : get the "id" functionality.
     *
     * @param id the id of the functionality to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the functionality, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/functionalities/{id}")
    public ResponseEntity<Functionality> getFunctionality(@PathVariable Long id) {
        log.debug("REST request to get Functionality : {}", id);
        Optional<Functionality> functionality = functionalityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(functionality);
    }

    /**
     * {@code DELETE  /functionalities/:id} : delete the "id" functionality.
     *
     * @param id the id of the functionality to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/functionalities/{id}")
    public ResponseEntity<Void> deleteFunctionality(@PathVariable Long id) {
        log.debug("REST request to delete Functionality : {}", id);
        functionalityService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
