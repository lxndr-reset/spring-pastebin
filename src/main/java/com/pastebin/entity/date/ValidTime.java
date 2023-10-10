package com.pastebin.entity.date;

import java.sql.Timestamp;
import java.util.Calendar;

import static java.util.Calendar.HOUR;

public enum ValidTime {
    TEN_SECONDS(1/60/60), ONE_HOUR(1), ONE_DAY(24), ONE_WEEK(24 * 7), TWO_WEEKS(24 * 7 * 2), ONE_MONTH(24 * 7 * 4), THREE_MONTHS(24 * 7 * 4 * 3);
    private final int hoursDuration;

    ValidTime(int hours) {
        hoursDuration = hours;
    }

    public int getHoursDuration() {
        return hoursDuration;
    }

    public Timestamp toTimeStamp() {
        Calendar cal = Calendar.getInstance();
        cal.add(HOUR, this.hoursDuration);
        return new Timestamp(cal.getTimeInMillis());
    }
}