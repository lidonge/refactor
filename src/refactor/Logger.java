package refactor;


import org.slf4j.LoggerFactory;

public class Logger {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("refactor.BatchRefactor");

    public static void log(Status status) {
        switch (status.getSeverity()) {
            case Status.ERROR:
                LOG.error(status.getMessage(), status.getException());
                break;
            case Status.INFO:
                LOG.info(status.getMessage(), status.getException());
                break;
        }
    }

    public static void log( String message) {
        log(Status.INFO ,message);
    }
    public static void log(int severity, String message) {
        log(new Status(severity, message));
    }

    public static void log(int severity, String message, Throwable t) {
        log(severity, message, t);
    }

    public static void log(int severity, int code, String message, Throwable t) {
        log(new Status(severity, message, t));
    }
}
