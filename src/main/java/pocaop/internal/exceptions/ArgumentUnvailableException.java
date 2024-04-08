package pocaop.internal.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArgumentUnvailableException extends ArgumentException {
    private final String type;
    private final int position;

    @Override
    public String specializedMessage() {
        return "does not exist";
    }
}
