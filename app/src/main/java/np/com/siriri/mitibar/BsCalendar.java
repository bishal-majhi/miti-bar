package np.com.siriri.mitibar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class BsCalendar {

    public static class BsDateResult {
        public String monthShort;     // e.g. "असार"
        public String dayNum;         // e.g. "१५"
        public int dayOfMonth;        // e.g. 15 (1..32, or 0 if out of range)
        public String dateOnly;       // e.g. "असार १५, २०८३"
        public String weekday;        // e.g. "मंगलबार"
        public String fullLockscreen; // e.g. "असार १५, २०८३ • मंगलबार"
        public boolean inRange;
    }

    private static final String[] MONTHS = {
        "बैशाख", "जेठ", "असार", "साउन", "भदौ", "असोज",
        "कार्तिक", "मंसिर", "पुष", "माघ", "फागुन", "चैत"
    };

    private static final String[] WEEKDAYS = {
        "आइतबार", "सोमबार", "मंगलबार", "बुधबार", "बिहीबार", "शुक्रबार", "शनिबार"
    };

    // BS month lengths from 2070 BS to 2095 BS.
    // Note: Years after 2083 BS use an approximate repeating pattern and should be refreshed
    // against official Nepal Panchanga tables as future calendars are published.
    private static final int START_BS_YEAR = 2070;
    private static final int[][] BS_MONTH_DAYS = {
        {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}, // 2070
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2071
        {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}, // 2072
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}, // 2073
        {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}, // 2074
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2075
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}, // 2076
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}, // 2077
        {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}, // 2078
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2079
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}, // 2080
        {31, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30}, // 2081
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2082
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2083
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}, // 2084
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}, // 2085
        {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}, // 2086
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2087
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}, // 2088
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}, // 2089
        {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}, // 2090
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2091
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}, // 2092
        {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}, // 2093
        {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}, // 2094
        {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}  // 2095
    };

    public static String toNepaliDigits(int number) {
        String eng = String.valueOf(number);
        char[] nepals = {'०', '१', '२', '३', '४', '५', '६', '७', '८', '९'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < eng.length(); i++) {
            char c = eng.charAt(i);
            if (c >= '0' && c <= '9') {
                sb.append(nepals[c - '0']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static final String NPT_TZ_ID = "Asia/Kathmandu";

    public static TimeZone getNptTimeZone() {
        return TimeZone.getTimeZone(NPT_TZ_ID);
    }

    public static BsDateResult getToday() {
        return getToday(true);
    }

    public static BsDateResult getToday(boolean useNepalTime) {
        TimeZone tz = useNepalTime ? getNptTimeZone() : TimeZone.getDefault();
        return fromGregorian(Calendar.getInstance(tz));
    }

    public static BsDateResult fromGregorian(Calendar target) {
        // Preserve the target calendar's own date fields, then compare at noon NPT (DST-free)
        // so day-count arithmetic is exact regardless of caller timezone or local DST shifts.
        TimeZone npt = getNptTimeZone();
        Calendar day = new GregorianCalendar(npt);
        day.set(
                target.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH),
                12, 0, 0
        );
        day.set(Calendar.MILLISECOND, 0);

        // Anchor: 2070-01-01 BS == 2013-04-14 AD
        Calendar anchor = new GregorianCalendar(npt);
        anchor.set(2013, Calendar.APRIL, 14, 12, 0, 0);
        anchor.set(Calendar.MILLISECOND, 0);

        long diffMillis = day.getTimeInMillis() - anchor.getTimeInMillis();
        long remainingDays = Math.round((double) diffMillis / (24 * 60 * 60 * 1000L));

        int dayOfWeekIndex = day.get(Calendar.DAY_OF_WEEK) - 1;
        String dayName = (dayOfWeekIndex >= 0 && dayOfWeekIndex < WEEKDAYS.length) ? WEEKDAYS[dayOfWeekIndex] : "वार";

        if (remainingDays < 0) {
            return outOfRangeResult(dayName);
        }

        for (int y = 0; y < BS_MONTH_DAYS.length; y++) {
            for (int m = 0; m < 12; m++) {
                int daysInMonth = BS_MONTH_DAYS[y][m];
                if (remainingDays < daysInMonth) {
                    int bsYear = START_BS_YEAR + y;
                    int bsDay = (int) remainingDays + 1;

                    BsDateResult res = new BsDateResult();
                    res.inRange = true;
                    res.dayOfMonth = bsDay;
                    res.monthShort = MONTHS[m];
                    res.dayNum = toNepaliDigits(bsDay);
                    res.dateOnly = res.monthShort + " " + res.dayNum + ", " + toNepaliDigits(bsYear);
                    res.weekday = dayName;
                    res.fullLockscreen = res.dateOnly + " • " + dayName;
                    return res;
                }
                remainingDays -= daysInMonth;
            }
        }

        return outOfRangeResult(dayName);
    }

    private static BsDateResult outOfRangeResult(String dayName) {
        BsDateResult res = new BsDateResult();
        res.inRange = false;
        res.dayOfMonth = 0;
        res.monthShort = "मिति";
        res.dayNum = "--";
        res.dateOnly = "मिति दायरा बाहिर (२०७०–२०९५)";
        res.weekday = dayName;
        res.fullLockscreen = res.dateOnly + " • " + dayName;
        return res;
    }
}
