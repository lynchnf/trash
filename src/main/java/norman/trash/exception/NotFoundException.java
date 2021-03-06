package norman.trash.exception;

import org.slf4j.Logger;

import static norman.trash.MessagesConstants.NOT_FOUND_ERROR;

public class NotFoundException extends Exception {
    public NotFoundException(Logger logger, String entityName, long id) {
        super(String.format(NOT_FOUND_ERROR, entityName, id));
        logger.warn(this.getMessage());
    }
}
