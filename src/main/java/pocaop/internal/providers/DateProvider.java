package pocaop.internal.providers;

import pocaop.internal.exceptions.ArgumentException;

import java.time.LocalDate;
import java.util.Optional;

public interface DateProvider {
    Optional<LocalDate> provide(Object[] arguments)  throws ArgumentException;
}
