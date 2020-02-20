package norman.trash.exception;

import norman.trash.service.OfxParseState;
import org.slf4j.Logger;

import static norman.trash.MessagesConstants.PARSE_BAD_TOKEN_ERROR;

public class OfxParseBadTokenException extends OfxParseException {
    public OfxParseBadTokenException(Logger logger, OfxParseState state, String line) {
        super(logger, String.format(PARSE_BAD_TOKEN_ERROR, state, line));
    }
}
