package poc.methodinjection.jdkproxy;

import example.controllers.CommuneApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import poc.methodinjection.BeanDefinitionProviderWithJdkProxy;
import poc.methodinjection.RegistrarForBeansWithInjectedMethods;
import poc.methodinjection.RequestProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = JdkProxySimpleTest.JdkProxyTestConfiguration.class, properties = "pocaop.interface-controllers.package=example.controllers")
@AutoConfigureMockMvc
class JdkProxySimpleTest {

    @Test
    void testJdkProxy(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/communes/33529"))
                .andExpect(status().isOk())
                .andExpect(content().string("[GET] Commune with arguments [code = 33529]"));
    }

    @Test
    void verifyProxy(@Autowired CommuneApi communeApi) {
        assertThat(communeApi.hashCode()).isEqualTo(System.identityHashCode(communeApi));
        assertThat(communeApi.toString().equals(STR."example.controllers.CommuneApi&\{communeApi.getClass().getName()}")).isTrue();
    }

    @Configuration(proxyBeanMethods = false)
    static class JdkProxyTestConfiguration{

        @Bean(RegistrarForBeansWithInjectedMethods.BEAN_NAME)
        public RegistrarForBeansWithInjectedMethods registrarForBeansWithInjectedMethods(){
            return new RegistrarForBeansWithInjectedMethods( new BeanDefinitionProviderWithJdkProxy());
        }

        @Bean
        public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
            return http.authorizeHttpRequests(authorize -> authorize
                    .anyRequest().permitAll()
            ).build();
        }

    }

}
