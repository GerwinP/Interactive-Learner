/**
 * Created by Gerwin Puttenstein on 23-1-2017.
 * An enumerate of codes that are used as responses from the observables to the observers.
 */
public enum Codes {

    ADDING (1),
    ADDED (2),
    CLASSIFIED(3),
    TRAINING(4),
    TRAINED(5),
    VERIFIED(6),
    ERROR (-1);

    private final int code;
    private String arg;

    Codes(int code) { this.code = code; this.arg = ""; }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public String getArg() {
        return this.arg;
    }
}
