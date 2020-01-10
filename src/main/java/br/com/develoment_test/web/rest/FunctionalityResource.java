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
 * GraphQL controller for managing {@link br.com.develoment_test.domain.Functionality}.
 */
@Component
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
     * {@code GraphQL createFunctionality } : Create a new functionality.
     *
     * @param functionality the functionality to create.
     */
    public Optional<Functionality> createFunctionality(@Valid Functionality functionality) throws URISyntaxException {
        if (functionality.getId() != null) {
            throw new BadRequestAlertException("A new functionality cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return functionalityService.save(functionality);
    }

    /**
     * {@code GraphQL updateFunctionality } : Updates an existing functionality.
     *
     * @param functionality the functionality to update.
     */
    public Optional<Functionality> updateFunctionality(@Valid Functionality functionality) throws URISyntaxException {
        if (functionality.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        return functionalityService.save(functionality);
    }

    /**
     * {@code GrahpQL getAllFunctionalities } : get all the functionalities.
     */
    public List<Functionality> getAllFunctionalities() {
        return functionalityService.findAll();
    }

    /**
     * {@code GraphQLgetFunctionality } : get the "id" functionality.
     *
     * @param id the id of the functionality to retrieve.
     */
    public Optional<Functionality> getFunctionality(Long id) {
        return functionalityService.findOne(id); 
    }

    /**
     * {@code GraphQL deleteFunctionality } : delete the "id" functionality.
     *
     * @param id the id of the functionality to delete.
     */
    public String deleteFunctionality(Long id) {
        functionalityService.delete(id);
        return ENTITY_NAME + ": " + id.toString();
    }
}
