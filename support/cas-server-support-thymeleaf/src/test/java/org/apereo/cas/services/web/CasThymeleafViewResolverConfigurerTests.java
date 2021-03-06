package org.apereo.cas.services.web;

import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreViewsConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasThymeleafConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.services.web.config.CasThemesConfiguration;
import org.apereo.cas.validation.CasProtocolViewFactory;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.View;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;
import org.thymeleaf.spring5.view.ThymeleafView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link CasThymeleafViewResolverConfigurerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    ThymeleafAutoConfiguration.class,
    CasThymeleafViewResolverConfigurerTests.CasThymeleafViewResolverConfigurerTestConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasCoreTicketIdGeneratorsConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreLogoutConfiguration.class,
    CasPersonDirectoryTestConfiguration.class,
    CasCoreAuthenticationConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class,
    CasCoreAuthenticationSupportConfiguration.class,
    CasCoreAuthenticationMetadataConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreWebConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreNotificationsConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasThemesConfiguration.class,
    CasThymeleafConfiguration.class,
    CasCoreViewsConfiguration.class
}, properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "cas.custom.properties.test=test"
})
@Tag("Web")
public class CasThymeleafViewResolverConfigurerTests {

    @Autowired
    @Qualifier("thymeleafViewResolver")
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    @Qualifier("themeViewResolverFactory")
    private ThemeViewResolverFactory themeViewResolverFactory;

    @Test
    public void verifyOperation() throws Exception {
        val view = thymeleafViewResolver.resolveViewName("testTemplate", Locale.ENGLISH);
        assertNotNull(view);
        assertTrue(((ThymeleafView) view).getStaticVariables().containsKey("cas"));
        assertTrue(((ThymeleafView) view).getStaticVariables().containsKey("casProperties"));
        
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));

        view.render(Map.of(), request, response);
        val body = response.getContentAsString();
        assertNotNull(body);
    }

    @Test
    public void verifyDirectView() throws Exception {
        val resolver = themeViewResolverFactory.create("cas-theme-default");
        val view = resolver.resolveViewName("oneCustomView", Locale.ENGLISH);
        assertNotNull(view);
        assertTrue(((ThymeleafView) view).getStaticVariables().containsKey("cas"));
        assertTrue(((ThymeleafView) view).getStaticVariables().containsKey("casProperties"));
    }

    @TestConfiguration
    public static class CasThymeleafViewResolverConfigurerTestConfiguration {
        @Autowired
        @Qualifier("casProtocolViewFactory")
        private CasProtocolViewFactory casProtocolViewFactory;

        @Autowired
        private ConfigurableApplicationContext applicationContext;
        
        @Bean
        public View oneCustomView() {
            return casProtocolViewFactory.create(applicationContext, "testTemplate");
        }
    }
}
