package pocaop.internal.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BadArgumentTypeException extends ArgumentException {
    private final String type;
    private final Object argument;
    private final int position;

    @Override
    public String specializedMessage() {
        return "has not the right type (actual value is " + argument + ")";
    }
}
