package com.github.elgashu;

import java.io.Closeable;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

public class Progress implements Closeable
{
    private final Instant start;
    private final long onePercent;
    private long progress;
    private int progressPercent;
    private long goal;

    public Progress(long goal)
    {
        start = Instant.now();
        this.goal = goal;
        onePercent = goal / 100;
        System.out.print("0%\r");
    }

    public void update()
    {
        progress++;
        if (progress % onePercent == 0)
        {
            System.out.print(
                MessageFormat.format(
                    "{0}%, {1} remaining\r", ++progressPercent, Durations.format(calculateRemaining())));
        }
    }

    private Duration calculateRemaining()
    {
        Duration elapsed = Duration.between(start, Instant.now());
        long remaining = goal - progress;
        return elapsed.dividedBy(progress).multipliedBy(remaining);
    }

    @Override
    public void close() throws IOException
    {
        System.out.println("100%");
    }
}
