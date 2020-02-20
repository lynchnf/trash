package norman.trash.exception;

import norman.trash.service.OfxParseState;
import org.slf4j.Logger;

import static norman.trash.MessagesConstants.PARSE_MISSING_TOKEN_ERROR;

public class OfxParseMissingTokenException extends OfxParseException {
    public OfxParseMissingTokenException(Logger logger, OfxParseState state, String line) {
        super(logger, String.format(PARSE_MISSING_TOKEN_ERROR, state, line));
    }
}
