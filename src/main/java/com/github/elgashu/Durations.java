package com.github.elgashu;

import java.time.Duration;

import org.apache.commons.lang3.time.DurationFormatUtils;

public final class Durations
{
    private Durations()
    {
    }

    public static String format(Duration duration)
    {
        long millis = duration.toMillis();
        if (millis >= 1000)
        {
            return DurationFormatUtils.formatDurationWords(millis, true, true);
        }
        else
        {
            return millis + " milliseconds";
        }
    }
}
