package main;

/**
 * Created by morev on 10.04.16.
 */
public enum ResponseCode {
    OK(0),
    NotFound(1),
    InvalidRequest(2),
    IncorrectRequest(3),
    UnknownError(4),
    UserAlreadyExists(5);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int value() {
        return code;
    }
}
