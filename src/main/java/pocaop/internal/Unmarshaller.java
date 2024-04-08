package pocaop.internal;

import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.lang.reflect.Method;

public interface Unmarshaller {
    @Nullable
    Object unmarshal(@NonNull String csv, @NonNull Method calledMethod);
}
