package com.demo.statistics.utils;

public class TimeUtils {

    public static final long ONE_MIN_IN_SEC = 60;

    public static Long getTimestampInSecond(Long timestamp) {
        return timestamp / 1000; // get second
    }

    public static boolean isEventTimeWithinPastSixtySeconds(long currentTimeStamp, long eventTimeStamp) {
        var diff = currentTimeStamp - eventTimeStamp;
        return diff >= 0 && diff < ONE_MIN_IN_SEC;
    }
}
