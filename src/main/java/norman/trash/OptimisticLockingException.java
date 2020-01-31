package norman.trash;

import org.slf4j.Logger;

import static norman.trash.MessagesConstants.OPTIMISTIC_LOCKING_ERROR;

public class OptimisticLockingException extends Exception {
    public OptimisticLockingException(Logger logger, String entityName, long id, Throwable cause) {
        super(String.format(OPTIMISTIC_LOCKING_ERROR, entityName, id), cause);
        logger.warn(this.getMessage(), this.getCause());
    }
}
