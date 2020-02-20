package norman.trash.exception;

import org.slf4j.Logger;

import static norman.trash.MessagesConstants.PARSE_ERROR;

public class OfxParseException extends Exception {
    public OfxParseException(Logger logger, String message) {
        super(message);
        logger.error(message);
    }

    public OfxParseException(Logger logger, String field, String line, Throwable cause) {
        super(String.format(PARSE_ERROR, field, line), cause);
        logger.error(this.getMessage(), cause);
    }
}
