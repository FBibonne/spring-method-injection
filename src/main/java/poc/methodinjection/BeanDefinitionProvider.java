package poc.methodinjection;

import org.springframework.core.type.AnnotationMetadata;

import java.util.Optional;

public interface BeanDefinitionProvider {
    Optional<BeanDefinitionWithName> provide(AnnotationMetadata classMetadata);
}
