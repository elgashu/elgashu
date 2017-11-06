package com.github.elgashu;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

import javax.xml.bind.DatatypeConverter;

import com.google.common.primitives.UnsignedBytes;

public class Lookup implements AutoCloseable
{
    private static final int HASH_BYTES = 20;
    private static final Comparator<byte[]> COMPARATOR = UnsignedBytes.lexicographicalComparator();
    private final Index index;

    public static void main(String[] args) throws IOException
    {
        Path dataFile = Paths.get(args[0]);
        Path indexFile = Index.getIndexFile(dataFile);
        try (Lookup instance = new Lookup(dataFile, indexFile))
        {
            Instant start = Instant.now();
            boolean result = instance.lookup(args[1]);
            Duration duration = Duration.between(start, Instant.now());

            System.out.println(
                MessageFormat.format(
                    "Searched {0} hashes in {1}",
                    instance.getHashCount(),
                    Durations.format(duration)));
            System.out.println(MessageFormat.format("Result: {0}", result));
        }
    }

    private final RandomAccessFile dataFile;

    private final int hashCount;

    public Lookup(Path dataFile, Path indexFile) throws IOException
    {
        this.dataFile = new RandomAccessFile(dataFile.toFile(), "r");
        index = new Index(indexFile);
        hashCount = (int) (this.dataFile.length() / 20);
    }

    public int getHashCount()
    {
        return hashCount;
    }

    public boolean lookup(String hashString) throws IOException
    {
        byte[] hashBytes = DatatypeConverter.parseHexBinary(hashString);
        return lookup(hashBytes);
    }

    private boolean lookup(byte[] hashBytes) throws IOException
    {
        Index.Bounds bounds = index.getBounds(hashBytes);
        int lowIndex = bounds.getLower();
        int highIndex = bounds.getHigher();
        while (lowIndex <= highIndex)
        {
            int middleIndex = lowIndex + (highIndex - lowIndex) / 2;
            byte[] middleHash = readHashByIndex(middleIndex);
            int comparison = COMPARATOR.compare(hashBytes, middleHash);
            if (comparison < 0)
            {
                highIndex = middleIndex - 1;
            }
            else if (comparison > 0)
            {
                lowIndex = middleIndex + 1;
            }
            else
            {
                return true;
            }
        }
        return false;
    }

    private byte[] readHashByIndex(long index) throws IOException
    {
        byte[] result = new byte[HASH_BYTES];
        dataFile.seek(index * HASH_BYTES);
        dataFile.read(result, 0, HASH_BYTES);
        return result;
    }

    @Override
    public void close() throws IOException
    {
        dataFile.close();
    }
}
