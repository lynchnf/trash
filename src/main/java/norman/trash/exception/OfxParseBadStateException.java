package norman.trash.exception;

import norman.trash.domain.repository.OfxParseState;
import org.slf4j.Logger;

public class OfxParseBadStateException extends OfxParseException {
    public OfxParseBadStateException(Logger logger, OfxParseState state) {
        super(logger, String.format(msg2, state));
    }
}
