package poc.methodinjection.jdkproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration(proxyBeanMethods=false)
public class TestAot {
    public static void main(String[] args) {
        SpringApplication.run(TestAot.class, "--pocaop.interface-controllers.package=poc.methodinjection.example.controllers");
    }

    @Bean(RegistrarForBeansWithInjectedMethods.BEAN_NAME)
    public RegistrarForBeansWithInjectedMethods registrarForBeansWithInjectedMethods() {
        return new RegistrarForBeansWithInjectedMethods(new BeanDefinitionProviderWithJdkProxy());
    }
}