package fr.bibonne.testaop;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TestAop {

    public static void main(String[] args) {
        configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
    }

    private static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(TestAop.class)
                .listeners(new PropertiesLogger(), new ControllersConfiguration());
    }

}

