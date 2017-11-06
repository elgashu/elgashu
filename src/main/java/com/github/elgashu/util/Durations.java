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
package com.github.elgashu.util;

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
