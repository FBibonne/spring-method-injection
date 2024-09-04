package poc.methodinjection;

import org.springframework.beans.factory.config.BeanDefinition;

public record BeanDefinitionWithName(String name, BeanDefinition beanDefinition) {
}
