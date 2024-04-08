package pocaop.internal;

import lombok.NonNull;

public interface QueryExecutor{
    String execute(@NonNull String query);
}
