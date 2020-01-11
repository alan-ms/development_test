package br.com.develoment_test.web.rest;

import br.com.develoment_test.domain.Functionality;
import br.com.develoment_test.security.AuthoritiesConstants;
import br.com.develoment_test.service.FunctionalityService;
import br.com.develoment_test.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
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
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"createUser\", \"" + AuthoritiesConstants.ADMIN + "\") != null")
    public Functionality createFunctionality(@Valid Functionality functionality) throws URISyntaxException {
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
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"createUser\", \"" + AuthoritiesConstants.ADMIN + "\") != null")
    public Functionality updateFunctionality(@Valid Functionality functionality) throws URISyntaxException {
        if (functionality.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        return functionalityService.save(functionality);
    }

    /**
     * {@code GrahpQL getAllFunctionalities } : get all the functionalities.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"createUser\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    public List<Functionality> getAllFunctionalities() {
        return functionalityService.findAll();
    }

    /**
     * {@code GraphQL getFunctionality } : get the "id" functionality.
     *
     * @param id the id of the functionality to retrieve.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"getFunctionality\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    public Optional<Functionality> getFunctionality(Long id) {
        return functionalityService.findOne(id);
    }

    /**
     * {@code GraphQL deleteFunctionality } : delete the "id" functionality.
     *
     * @param id the id of the functionality to delete.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"getFunctionality\", \"" + AuthoritiesConstants.ADMIN + "\") != null")
    public String deleteFunctionality(Long id) {
        functionalityService.delete(id);
        return ENTITY_NAME + ": " + id.toString();
    }
}
