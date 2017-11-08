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
import java.io.RandomAccessFile;
import java.nio.file.Path;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class ArchiveConverter
{
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
            RandomAccessFile randomAccessFile = new RandomAccessFile(archivePath.toFile(), "r");
            IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            DatabaseCreator databaseCreator = new DatabaseCreator(
                targetPath, indexFile, indexInterval))
        {
            ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simpleInterface.getArchiveItems())
            {
                try (ArchiveProcessor archiveProcessor = new ArchiveProcessor(item.getSize(), databaseCreator))
                {
                    ExtractOperationResult result = item.extractSlow(archiveProcessor);
                    if (result != ExtractOperationResult.OK)
                    {
                        System.err.println("Error extracting item: " + result);
                    }
                }
            }
        }
    }
}
