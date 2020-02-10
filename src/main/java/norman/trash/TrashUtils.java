package norman.trash;

import java.util.Calendar;
import java.util.Date;

public final class TrashUtils {
    private static Date endOfTime;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        endOfTime = cal.getTime();
    }

    private TrashUtils() {
    }

    public static Date getEndOfTime() {
        return endOfTime;
    }
}
