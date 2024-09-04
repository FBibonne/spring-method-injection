package poc.methodinjection.jdkproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import poc.methodinjection.BeanDefinitionProviderWithJdkProxy;
import poc.methodinjection.RegistrarForBeansWithInjectedMethods;
import poc.methodinjection.RequestProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestJdkProxy.JdkProxyTestConfiguration.class)
@AutoConfigureMockMvc
class TestJdkProxy {

    @Test
    void testJdkProxy(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/communes/33529")).andExpect(status().isOk()).andExpect(content().string("GET Commune with arguments [id = 33529]"));
    }

    @Configuration(proxyBeanMethods = false)
    static class JdkProxyTestConfiguration{

        @Bean
        public RegistrarForBeansWithInjectedMethods registrarForBeansWithInjectedMethods(){
            return new RegistrarForBeansWithInjectedMethods( new BeanDefinitionProviderWithJdkProxy(new RequestProcessor()));
        }
    }

}
