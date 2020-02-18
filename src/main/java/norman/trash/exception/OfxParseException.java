package norman.trash.exception;

import org.slf4j.Logger;

public class OfxParseException extends Exception {
    public static final String msg1 = "Error parsing %s in line=\"%s\".";
    public static final String msg2 = "Invalid state=\"%s\".";
    public static final String msg3 = "Invalid token found: state=\"%s\", line=\"%s\".";
    public static final String msg4 = "No valid token found: state=\"%s\", line=\"%s\".";

    public OfxParseException(Logger logger, String message) {
        super(message);
        logger.error(message);
    }

    public OfxParseException(Logger logger, String field, String line, Throwable cause) {
        super(String.format(msg1, field, line), cause);
        logger.error(this.getMessage(), cause);
    }
}
