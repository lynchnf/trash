package norman.trash.exception;

import norman.trash.domain.repository.OfxParseState;
import org.slf4j.Logger;

public class OfxParseBadTokenException extends OfxParseException {
    public OfxParseBadTokenException(Logger logger, OfxParseState state, String line) {
        super(logger, String.format(msg3, state, line));
    }
}
