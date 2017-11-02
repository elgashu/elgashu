package com.github.elgashu;

import java.io.IOException;
import java.io.RandomAccessFile;
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

    public static void main(String[] args) throws IOException
    {
        try (Lookup instance = new Lookup(args[0]))
        {
            Instant start = Instant.now();
            boolean result = instance.lookup(args[1]);
            Duration duration = Duration.between(start, Instant.now());

            System.out.println(result);
            System.out.println(
                MessageFormat.format(
                    "Stats: searched {0} hashes in {1}", instance.getHashCount(), Durations.format(duration)));
        }
    }

    private final RandomAccessFile dataFile;

    private final int hashCount;

    public Lookup(String dataFilePath) throws IOException
    {
        dataFile = new RandomAccessFile(dataFilePath, "r");
        hashCount = (int) (dataFile.length() / 20);
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
        long lowIndex = 0;
        long highIndex = getHashCount() - 1;
        while (lowIndex <= highIndex)
        {
            long middleIndex = lowIndex + (highIndex - lowIndex) / 2;
            int comparison = COMPARATOR.compare(hashBytes, readHashByIndex(middleIndex));
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
