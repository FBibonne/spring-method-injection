package pocaop.internal.queries;

import lombok.NonNull;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Optional.of;

public final class FindByCodeQuery extends SimpleQuery {

    public static final FindByCodeQuery INSTANCE = new FindByCodeQuery();

    public String interpolate(@NonNull String territoire, @NonNull String code, Optional<LocalDate> date) {
        return super.interpolate(of(code), date, territoire);
    }
}
