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

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.MessageFormat;

import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

class ArchiveProcessor implements ISequentialOutStream, Closeable
{
    private static final int BINARY_HASH_LENGTH = 20;
    private static final int HEX_HASH_LENGTH = BINARY_HASH_LENGTH * 2;
    private static final int EOL_LENGTH = 2;
    private static final int HEX_HASH_AND_EOL_LENGTH = HEX_HASH_LENGTH + EOL_LENGTH;

    private byte leadingHexByte;
    private DatabaseCreator databaseCreator;
    private Progress progress;

    private byte[] hash = new byte[BINARY_HASH_LENGTH];
    private int bytesInHash;

    public ArchiveProcessor(long size, DatabaseCreator databaseCreator)
    {
        System.out.println(MessageFormat.format("Number of hashes: {0}", size / HEX_HASH_AND_EOL_LENGTH));
        this.databaseCreator = databaseCreator;
        progress = new Progress(size);
    }

    @Override
    public int write(byte[] hexBytes) throws SevenZipException
    {
        for (byte b : hexBytes)
        {
            write(b);
            progress.update();
        }

        return hexBytes.length;
    }

    private void write(byte hexByte)
    {
        if (!isEol(hexByte))
        {
            if (leadingHexByte == 0)
            {
                leadingHexByte = hexByte;
            }
            else
            {
                byte result = readHexSequence(leadingHexByte, hexByte);
                leadingHexByte = 0;

                addToHash(result);
            }
        }
    }

    private boolean isEol(byte b)
    {
        return b == (byte) 13 || b == (byte) 10;
    }

    private byte readHexSequence(byte first, byte second)
    {
        return (byte) (fromHex(first) * 16 + fromHex(second));
    }

    private int fromHex(int b)
    {
        switch (b)
        {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return b - '0';
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                return b - 'A' + 10;
            default:
                throw new IllegalArgumentException("byte " + b + " is not a valid hex character");
        }
    }

    private void addToHash(byte b)
    {

        hash[bytesInHash++] = b;
        if (bytesInHash == BINARY_HASH_LENGTH)
        {
            writeToDatabase();
            bytesInHash = 0;
        }
    }

    private void writeToDatabase()
    {
        try
        {
            databaseCreator.add(hash);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException
    {
        progress.close();
    }
}
