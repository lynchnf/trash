package norman.trash.exception;

import norman.trash.domain.repository.OfxParseState;
import org.slf4j.Logger;

public class OfxParseMissingTokenException extends OfxParseException {
    public OfxParseMissingTokenException(Logger logger, OfxParseState state, String line) {
        super(logger, String.format(msg4, state, line));
    }
}
