package br.com.develoment_test.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import br.com.develoment_test.web.rest.TestUtil;

public class FunctionalityTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Functionality.class);
        Functionality functionality1 = new Functionality();
        functionality1.setId(1L);
        Functionality functionality2 = new Functionality();
        functionality2.setId(functionality1.getId());
        assertThat(functionality1).isEqualTo(functionality2);
        functionality2.setId(2L);
        assertThat(functionality1).isNotEqualTo(functionality2);
        functionality1.setId(null);
        assertThat(functionality1).isNotEqualTo(functionality2);
    }
}
