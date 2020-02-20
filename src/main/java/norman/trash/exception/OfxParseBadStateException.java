package norman.trash.exception;

import norman.trash.service.OfxParseState;
import org.slf4j.Logger;

import static norman.trash.MessagesConstants.PARSE_BAD_STATE_ERROR;

public class OfxParseBadStateException extends OfxParseException {
    public OfxParseBadStateException(Logger logger, OfxParseState state) {
        super(logger, String.format(PARSE_BAD_STATE_ERROR, state));
    }
}
