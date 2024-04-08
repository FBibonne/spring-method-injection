package pocaop.unmarshaller;

import lombok.NonNull;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.util.Collection;

public record ReturnedType(@NonNull ResolvableType resolvableType) {

    public ReturnedType(@NonNull Method calledMethod){
        this(ResolvableType.forMethodReturnType(calledMethod));
    }

    public Class<?> typeForMapping() {
        return resolvableType.hasGenerics()?resolvableType.getGeneric().resolve():resolvableType.resolve();
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(resolvableType.resolve());
    }
}
