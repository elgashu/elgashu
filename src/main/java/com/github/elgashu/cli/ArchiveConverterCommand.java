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
package com.github.elgashu.cli;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;

import com.github.elgashu.core.ArchiveConverter;
import com.github.elgashu.core.Index;
import com.github.elgashu.util.Durations;

public class ArchiveConverterCommand
{
    @Command
    public void convertArchive(
        @Option("archive") @Required File archive,
        @Option("dataFile") @Required File dataFile,
        @Option("indexInterval") @Default("10000") int indexInterval) throws IOException
    {
        Instant start = Instant.now();

        new ArchiveConverter(
            archive.toPath(), dataFile.toPath(), Index.getIndexFile(dataFile.toPath()), indexInterval).run();

        Duration duration = Duration.between(start, Instant.now());
        System.out.println("Duration: " + Durations.format(duration));
    }
}
