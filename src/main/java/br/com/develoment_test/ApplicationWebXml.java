package br.com.develoment_test;

import io.github.jhipster.config.DefaultProfileUtil;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * This is a helper Java class that provides an alternative to creating a {@code web.xml}.
 * This will be invoked only when the application is deployed to a Servlet container like Tomcat, JBoss etc.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        /**
         * set a default to use when no profile is configured.
         */
        DefaultProfileUtil.addDefaultProfile(application.application());
        return application.sources(DevelopmentTestApp.class);
    }

//    @Bean
//    public CommandLineRunner run(AuthorityRepository authorityRepository, FunctionalityRepository functionalityRepository) throws Exception{
//        return (String[] args) -> {
//            Authority authority1 = new Authority(AuthoritiesConstants.ANONYMOUS);
//            Functionality functionality1 = new Functionality(1001L, "registerAccount", authority1);
//            System.out.println("aqui!");
//        };
//    }
}
