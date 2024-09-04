package poc.methodinjection.jdkproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import poc.methodinjection.BeanDefinitionProviderWithJdkProxy;
import poc.methodinjection.RegistrarForBeansWithInjectedMethods;
import poc.methodinjection.RequestProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestJdkProxyWithSecurity.JdkProxySecurityTestConfiguration.class,
properties = "pocaop.interface-controllers.package=example.controllerswithsecurity")
@AutoConfigureMockMvc
class TestJdkProxyWithSecurity {

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testJdkProxyWithGoodRole(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/communes/33529"))
                .andExpect(status().isOk())
                .andExpect(content().string("[GET] Commune with arguments [code = 33529]"));
    }

    @Test
    @WithMockUser()
    void testJdkProxyWithBadRole(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/communes/33529"))
                .andExpect(status().isForbidden());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebSecurity
    @EnableMethodSecurity(securedEnabled = true)
    static class JdkProxySecurityTestConfiguration {

        @Bean
        public RegistrarForBeansWithInjectedMethods registrarForBeansWithInjectedMethods(){
            return new RegistrarForBeansWithInjectedMethods( new BeanDefinitionProviderWithJdkProxy(new RequestProcessor()));
        }

    }

}
