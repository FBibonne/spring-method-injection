package pocaop;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.ResponseEntity;
import pocaop.configuration.PropertiesLogger;
import pocaop.example.controllers.CommuneApi;
import pocaop.example.model.Commune;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@SpringBootApplication
public class PocAop {

    public static void main(String[] s) {
        //configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
        InvocationHandler handler = (ignored, method, args) -> switch (method.getName()){
            case "getByCode" -> {
                var code = (String)args[0];
                if ("33529".equals(code)){
                    yield ResponseEntity.ok(new Commune("33529",  "La Teste de Buch"));
                }
                yield ResponseEntity.notFound().build();
            }
            default -> throw new IllegalStateException("Unexpected value: " + method.getName());
        };
        CommuneApi proxy = (CommuneApi) Proxy.newProxyInstance(PocAop.class.getClassLoader(), new Class[]{CommuneApi.class}, handler);
        System.out.println(proxy.getByCode(s[0]));
    }

    private static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(PocAop.class)
                .listeners(new PropertiesLogger(), new ControllersConfigurationListener());
    }


}

