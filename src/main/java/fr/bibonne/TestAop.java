package fr.bibonne;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Configuration
public class TestAop {
    public static void main(String[] args) {
        ApplicationContext context=new AnnotationConfigApplicationContext(TestAop.class);
        var singleton=context.getBean(TestSingleton.class);
        System.out.println(context.getBean(TestPrototype.class));
        System.out.println(context.getBean(TestPrototype.class));
        System.out.println();
        System.out.println(singleton);
        System.out.println(singleton.testPrototype());
        System.out.println(singleton.testPrototype());
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    TestPrototype testPrototype(){
        return new TestPrototype();
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    TestSingleton testSingleton(TestPrototype testPrototype){
        return new TestSingleton(testPrototype);
    }

    private static class TestPrototype {
    }

    private record TestSingleton(TestPrototype testPrototype) {
    }
}

