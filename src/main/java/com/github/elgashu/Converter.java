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
package com.github.elgashu;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class Converter
{
    public static final int INDEX_INTERVAL = Integer.parseInt(System.getProperty("indexInterval", "10000"));

    public static void main(String[] args) throws IOException
    {
        Instant start = Instant.now();

        new Converter(args[0], args[1]).run();

        Duration duration = Duration.between(start, Instant.now());
        System.out.println("Duration: " + Durations.format(duration));
    }

    private Path archivePath;
    private Path targetPath;

    public Converter(String archivePath, String targetPath)
    {
        this.archivePath = Paths.get(archivePath);
        this.targetPath = Paths.get(targetPath);
    }

    private void run() throws IOException
    {
        try (
            RandomAccessFile randomAccessFile = new RandomAccessFile(archivePath.toFile(), "r");
            IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            DatabaseCreator databaseCreator = new DatabaseCreator(INDEX_INTERVAL, targetPath))
        {
            ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simpleInterface.getArchiveItems())
            {
                try (HashFileProcessor hashFileProcessor = new HashFileProcessor(item.getSize(), databaseCreator))
                {
                    ExtractOperationResult result = item.extractSlow(hashFileProcessor);
                    if (result != ExtractOperationResult.OK)
                    {
                        System.err.println("Error extracting item: " + result);
                    }
                }
            }
        }
    }
}
