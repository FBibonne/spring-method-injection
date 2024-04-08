package pocaop.internal.providers;

import pocaop.internal.exceptions.ArgumentException;

public interface CodeProvider {
    String provide(Object[] arguments) throws ArgumentException;
}
