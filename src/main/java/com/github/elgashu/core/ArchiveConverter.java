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

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class ArchiveConverter
{
    private static final int BUFFER_SIZE = 8 * 1024;

    private final Path archivePath;
    private final Path targetPath;
    private final int indexInterval;
    private Path indexFile;

    public ArchiveConverter(Path archivePath, Path targetPath, Path indexFile, int indexInterval)
    {
        this.archivePath = archivePath;
        this.targetPath = targetPath;
        this.indexFile = indexFile;
        this.indexInterval = indexInterval;
    }

    public void run() throws IOException
    {
        try (
            SevenZFile archive = new SevenZFile(archivePath.toFile());
            DatabaseCreator databaseCreator = new DatabaseCreator(targetPath, indexFile, indexInterval))
        {
            SevenZArchiveEntry entry = archive.getNextEntry();
            try (ArchiveProcessor archiveProcessor = new ArchiveProcessor(entry.getSize(), databaseCreator))
            {
                byte[] buffer = new byte[BUFFER_SIZE];

                while (true)
                {
                    int bytesRead = archive.read(buffer, 0, buffer.length);
                    if (bytesRead == -1)
                    {
                        break;
                    }
                    archiveProcessor.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
