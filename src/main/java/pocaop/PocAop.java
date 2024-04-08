package pocaop;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import pocaop.configuration.PropertiesLogger;

@SpringBootApplication
public class PocAop {

    public static void main(String[] args) {
        configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
    }

    private static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(PocAop.class)
                .listeners(new PropertiesLogger(), new ControllersConfigurationListener());
    }

}

