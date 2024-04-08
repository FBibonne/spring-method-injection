package pocaop.internal.exceptions;

public abstract class ArgumentException extends Exception{

    public abstract int getPosition();
    public abstract String getType();
    public abstract String specializedMessage();

    @Override
    public String getMessage() {
        return STR."Argument of type \{getType()} at position \{getPosition()} : \{specializedMessage()}";
    }
}
