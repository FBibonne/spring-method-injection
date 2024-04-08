package pocaop.internal.providers;

import pocaop.internal.exceptions.ArgumentException;

public interface TerritoireProvider {
    String provide(Object[] arguments) throws ArgumentException;
}
