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
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;

import com.github.elgashu.core.Index;
import com.github.elgashu.core.Lookup;
import com.github.elgashu.util.Durations;

public class LookupCommand
{
    @Command
    public static void lookup(@Option("dataFile") @Required File dataFile, @Option("hash") @Required String hash)
        throws IOException
    {
        Path dataFilePath = dataFile.toPath();
        Index index = getIndex(dataFilePath);
        try (Lookup instance = new Lookup(dataFilePath, index))
        {
            Instant start = Instant.now();
            boolean result = instance.lookup(hash);
            Duration duration = Duration.between(start, Instant.now());

            System.out.println(
                MessageFormat.format(
                    "Searched {0} hashes in {1}", instance.getHashCount(), Durations.format(duration)));
            System.out.println(MessageFormat.format("Result: {0}", result));
        }
    }

    private static Index getIndex(Path dataFile) throws IOException
    {
        Path indexFile = Index.getIndexFile(dataFile);
        return new Index(indexFile);
    }

}
