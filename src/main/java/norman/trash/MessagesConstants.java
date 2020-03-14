package norman.trash;

public class MessagesConstants {
    public static final String NOT_FOUND_ERROR = "%s was not found for id=%d.";
    public static final String OPTIMISTIC_LOCKING_ERROR = "%s with id=%d was updated by another user.";
    public static final String MULTIPLE_OPTIMISTIC_LOCKING_ERROR = "One or more %s was update by another user.";
    public static final String BEGINNING_BALANCE = "beginning balance";
    public static final String SUCCESSFULLY_ADDED = "%s successfully added, id=%d.";
    public static final String SUCCESSFULLY_UPDATED = "%s successfully updated, id=%d.";
    public static final String SUCCESSFULLY_ADDED_ACCT = "Account %s was successfully added.";
    public static final String SUCCESSFULLY_UPDATED_ACCT = "Account %s was successfully updated.";
    public static final String MULTIPLE_SUCCESSFULLY_UPDATED = "Successfully updated multiple %s.";
    public static final String UPLOADED_FILE_NOT_FOUND_ERROR = "Upload file is empty or missing.";
    public static final String UPLOADED_FILE_NOT_READ_ERROR = "Error while opening or reading uploaded file.";
    public static final String UPLOADED_FILE_NOT_CLOSED_IGNORED = "Error ignored while closing uploaded file.";
    public static final String PARSE_ERROR = "Error parsing %s in line=\"%s\".";
    public static final String PARSE_BAD_STATE_ERROR = "Invalid state=\"%s\".";
    public static final String PARSE_BAD_TOKEN_ERROR = "Invalid token found: state=\"%s\", line=\"%s\".";
    public static final String PARSE_MISSING_TOKEN_ERROR = "No valid token found: state=\"%s\", line=\"%s\".";

    private MessagesConstants() {
    }
}
