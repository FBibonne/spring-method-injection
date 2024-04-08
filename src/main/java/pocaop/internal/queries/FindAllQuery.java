package pocaop.internal.queries;

import lombok.NonNull;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Optional.empty;

public final class FindAllQuery extends SimpleQuery {

    public static final FindAllQuery INSTANCE = new FindAllQuery();

    public String interpolate(@NonNull String territoire, Optional<LocalDate> date){
        return super.interpolate(empty(), date, territoire);
    }

}
