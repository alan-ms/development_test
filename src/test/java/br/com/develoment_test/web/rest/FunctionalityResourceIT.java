package br.com.develoment_test.web.rest;

import br.com.develoment_test.DevelopmentTestApp;
import br.com.develoment_test.domain.Functionality;
import br.com.develoment_test.domain.Authority;
import br.com.develoment_test.repository.FunctionalityRepository;
import br.com.develoment_test.service.FunctionalityService;
import br.com.develoment_test.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static br.com.develoment_test.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link FunctionalityResource} REST controller.
 */
@SpringBootTest(classes = DevelopmentTestApp.class)
public class FunctionalityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private FunctionalityService functionalityService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restFunctionalityMockMvc;

    private Functionality functionality;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FunctionalityResource functionalityResource = new FunctionalityResource(functionalityService);
        this.restFunctionalityMockMvc = MockMvcBuilders.standaloneSetup(functionalityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Functionality createEntity(EntityManager em) {
        Functionality functionality = new Functionality()
            .name(DEFAULT_NAME);
        // Add required entity
        Authority authority;
        if (TestUtil.findAll(em, Authority.class).isEmpty()) {
            authority = AuthorityResourceIT.createEntity(em);
            em.persist(authority);
            em.flush();
        } else {
            authority = TestUtil.findAll(em, Authority.class).get(0);
        }
        functionality.setAuthority(authority);
        return functionality;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Functionality createUpdatedEntity(EntityManager em) {
        Functionality functionality = new Functionality()
            .name(UPDATED_NAME);
        // Add required entity
        Authority authority;
        if (TestUtil.findAll(em, Authority.class).isEmpty()) {
            authority = AuthorityResourceIT.createUpdatedEntity(em);
            em.persist(authority);
            em.flush();
        } else {
            authority = TestUtil.findAll(em, Authority.class).get(0);
        }
        functionality.setAuthority(authority);
        return functionality;
    }

    @BeforeEach
    public void initTest() {
        functionality = createEntity(em);
    }

    @Test
    @Transactional
    public void createFunctionality() throws Exception {
        int databaseSizeBeforeCreate = functionalityRepository.findAll().size();

        // Create the Functionality
        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isCreated());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeCreate + 1);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createFunctionalityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = functionalityRepository.findAll().size();

        // Create the Functionality with an existing ID
        functionality.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = functionalityRepository.findAll().size();
        // set the field null
        functionality.setName(null);

        // Create the Functionality, which fails.

        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFunctionalities() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get all the functionalityList
        restFunctionalityMockMvc.perform(get("/api/functionalities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionality.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getFunctionality() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get the functionality
        restFunctionalityMockMvc.perform(get("/api/functionalities/{id}", functionality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(functionality.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    public void getNonExistingFunctionality() throws Exception {
        // Get the functionality
        restFunctionalityMockMvc.perform(get("/api/functionalities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFunctionality() throws Exception {
        // Initialize the database
        functionalityService.save(functionality);

        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();

        // Update the functionality
        Functionality updatedFunctionality = functionalityRepository.findById(functionality.getId()).get();
        // Disconnect from session so that the updates on updatedFunctionality are not directly saved in db
        em.detach(updatedFunctionality);
        updatedFunctionality
            .name(UPDATED_NAME);

        restFunctionalityMockMvc.perform(put("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFunctionality)))
            .andExpect(status().isOk());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();

        // Create the Functionality

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc.perform(put("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFunctionality() throws Exception {
        // Initialize the database
        functionalityService.save(functionality);

        int databaseSizeBeforeDelete = functionalityRepository.findAll().size();

        // Delete the functionality
        restFunctionalityMockMvc.perform(delete("/api/functionalities/{id}", functionality.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
