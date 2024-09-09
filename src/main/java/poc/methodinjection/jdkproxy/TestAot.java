package poc.methodinjection.jdkproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@SpringBootApplication
@Configuration(proxyBeanMethods=false)
public class TestAot {
    public static void main(String[] args) {
        var newArgs = Arrays.copyOf(args, args.length + 1);
        newArgs[args.length]="--pocaop.interface-controllers.package=poc.methodinjection.example.controllers";
        SpringApplication.run(TestAot.class, newArgs);
    }

    @Bean(RegistrarForBeansWithInjectedMethods.BEAN_NAME)
    public RegistrarForBeansWithInjectedMethods registrarForBeansWithInjectedMethods() {
        return new RegistrarForBeansWithInjectedMethods(new BeanDefinitionProviderWithJdkProxy());
    }
}