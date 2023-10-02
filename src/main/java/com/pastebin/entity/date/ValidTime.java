package com.pastebin.entity.date;

public enum ValidTime {
    ONE_HOUR(1), ONE_DAY(24), ONE_WEEK(24 * 7), TWO_WEEKS(24 * 7 * 2), ONE_MONTH (24 * 7 * 4), THREE_MONTHS(24 * 7 * 4 * 3);
    private final int hoursDuration;

    ValidTime(int hours) {
         hoursDuration = hours;
    }

    public int getHoursDuration() {
        return hoursDuration;
    }
}