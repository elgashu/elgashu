package com.github.elgashu;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
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
            OutputStream databaseOutputStream = new BufferedOutputStream(Files.newOutputStream(targetPath)))
        {
            ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simpleInterface.getArchiveItems())
            {
                try (HashFileProcessor hashFileProcessor = new HashFileProcessor(
                    item.getSize(), databaseOutputStream))
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
