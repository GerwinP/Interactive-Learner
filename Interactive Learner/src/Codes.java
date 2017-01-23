/**
 * Created by Gerwin on 23-1-2017.
 */
public enum Codes {

    ADDING (1),
    ADDED (2),
    ERROR (-1);

    private final int code;

    Codes(int code) {
        this.code = code;
    }
}
