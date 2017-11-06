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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.primitives.UnsignedBytes;

public class Index
{
    private static class Entry
    {
        private final byte[] value = new byte[BINARY_HASH_LENGTH];
        private final int position;

        public Entry(byte[] value, int position)
        {
            System.arraycopy(value, 0, this.value, 0, BINARY_HASH_LENGTH);
            this.position = position;
        }
    }

    public static class Bounds
    {
        private final int lower;
        private final int higher;

        public Bounds(int lower, int higher)
        {
            this.lower = lower;
            this.higher = higher;
        }

        public int getLower()
        {
            return lower;
        }

        public int getHigher()
        {
            return higher;
        }
    }

    private static final int BINARY_HASH_LENGTH = 20;
    private static final Comparator<byte[]> COMPARATOR = UnsignedBytes.lexicographicalComparator();

    public static Path getIndexFile(Path databaseFile)
    {
        return databaseFile.getParent().resolve(databaseFile.getFileName().toString() + ".index");
    }

    private List<Entry> entries = new ArrayList<>();

    public Index(Path indexFile) throws IOException
    {
        load(indexFile);
    }

    private void load(Path indexFile) throws IOException
    {
        byte[] hash = new byte[BINARY_HASH_LENGTH];
        try (DataInputStream stream = createInputStream(indexFile))
        {
            while (stream.available() > 0)
            {
                stream.read(hash);
                int position = stream.readInt();
                entries.add(new Entry(hash, position));
            }
        }
    }

    private DataInputStream createInputStream(Path indexFile) throws IOException
    {
        return new DataInputStream(new BufferedInputStream(Files.newInputStream(indexFile)));
    }

    /**
     * Determines the initial bounds for a binary search in the hashes. Note that {@link
     * com.github.elgashu.Index.Bounds} with equal <code>lower</code> and <code>higher</code> values do not imply that
     * the hash is found inside the database; only a subsequent search can determine this.
     *
     * @param hashBytes the hash to check
     *
     * @return the bounds, never <code>null</code>.
     */
    public Bounds getBounds(byte[] hashBytes) throws IOException
    {
        int testLow = 0;
        int testHigh = entries.size() - 1;

        int resultLow = testLow;
        int resultHigh = testHigh;
        while (testLow <= testHigh)
        {
            int middleIndex = testLow + (testHigh - testLow) / 2;
            Entry middleEntry = entries.get(middleIndex);
            int comparison = COMPARATOR.compare(hashBytes, middleEntry.value);
            if (comparison < 0)
            {
                testHigh = middleIndex - 1;
                resultHigh = middleIndex;
            }
            else if (comparison > 0)
            {
                testLow = middleIndex + 1;
                resultLow = middleIndex;
            }
            else
            {
                return new Bounds(middleEntry.position, middleEntry.position);
            }
        }
        return new Bounds(entries.get(resultLow).position, entries.get(resultHigh).position);
    }
}
