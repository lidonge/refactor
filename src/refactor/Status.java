package refactor;

public class Status {

    // Field descriptor #6 I
    public static final int OK = 0;

    // Field descriptor #6 I
    public static final int INFO = 1;

    // Field descriptor #6 I
    public static final int WARNING = 2;

    // Field descriptor #6 I
    public static final int ERROR = 4;

    // Field descriptor #6 I
    public static final int CANCEL = 8;

    private int severity;
    private String message;
    private Throwable t;
    public Status(int severity, String message, Throwable t) {
        this.severity = severity;
        this.message = message;
        this.t = t;
    }

    public Status(int severity, String message) {
        this.severity = severity;
        this.message = message;
    }

    public int getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getException() {
        return t;
    }

    @Override
    public String toString() {
        return "Status{" +
                "severity=" + severity +
                ", message='" + message + '\'' +
                ", t=" + t +
                '}';
    }
}
