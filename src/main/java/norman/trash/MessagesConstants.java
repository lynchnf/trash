package norman.trash;

public class MessagesConstants {
    public static final String NOT_FOUND_ERROR = "%s was not found for id=%d.";
    public static final String OPTIMISTIC_LOCKING_ERROR = "%s with id=%d was updated by another user.";
    public static final String MULTIPLE_OPTIMISTIC_LOCKING_ERROR = "One or more %s was update by another user.";
    public static final String BEGINNING_BALANCE = "beginning balance";
    public static final String SUCCESSFULLY_ADDED = "%s successfully added, id=%d.";
    public static final String SUCCESSFULLY_UPDATED = "%s successfully updated, id=%d.";
    public static final String SUCCESSFULLY_RECONCILED = "Account successfully reconciled, id=%d.";
    public static final String MULTIPLE_SUCCESSFULLY_UPDATED = "Successfully updated multiple %s.";

    private MessagesConstants() {
    }
}
