/**
 *  Copyright 2017 Jens Bannmann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.elgashu.core;

import java.io.Closeable;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import com.github.elgashu.util.Durations;

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
