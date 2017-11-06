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
import java.util.Comparator;

import javax.xml.bind.DatatypeConverter;

import com.google.common.primitives.UnsignedBytes;

public class Lookup implements AutoCloseable
{
    private static final int HASH_BYTES = 20;
    private static final Comparator<byte[]> COMPARATOR = UnsignedBytes.lexicographicalComparator();

    private final Index index;
    private final RandomAccessFile dataFile;
    private final int hashCount;

    public Lookup(Path dataFile, Index index) throws IOException
    {
        this.dataFile = new RandomAccessFile(dataFile.toFile(), "r");
        this.index = index;
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
