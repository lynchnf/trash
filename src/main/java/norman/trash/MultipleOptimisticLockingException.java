package norman.trash;

import org.slf4j.Logger;

import static norman.trash.MessagesConstants.MULTIPLE_OPTIMISTIC_LOCKING_ERROR;

public class MultipleOptimisticLockingException extends Exception {
    public MultipleOptimisticLockingException(Logger logger, String entityName, Throwable cause) {
        super(String.format(MULTIPLE_OPTIMISTIC_LOCKING_ERROR, entityName), cause);
        logger.warn(this.getMessage(), this.getCause());
    }
}
