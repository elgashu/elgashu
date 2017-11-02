package com.github.elgashu;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.text.MessageFormat;

import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class HashFileProcessor implements ISequentialOutStream, Closeable
{
    private static final int HASH_LENGTH = 40;
    private static final int EOL_LENGTH = 2;
    private static final int HASH_AND_EOL_LENGTH = HASH_LENGTH + EOL_LENGTH;

    private byte leadingByte;
    private OutputStream outputStream;
    private Progress progress;

    public HashFileProcessor(long size, OutputStream outputStream)
    {
        System.out.println(MessageFormat.format("Number of hashes: {0}", size / HASH_AND_EOL_LENGTH));
        this.outputStream = outputStream;
        progress = new Progress(size);
    }

    @Override
    public int write(byte[] bytes) throws SevenZipException
    {
        for (byte b : bytes)
        {
            write(b);
            progress.update();
        }

        return bytes.length;
    }

    private void write(byte b)
    {
        if (!isEol(b))
        {
            if (leadingByte == 0)
            {
                leadingByte = b;
            }
            else
            {
                byte result = readHexSequence(leadingByte, b);
                leadingByte = 0;

                writeToStream(result);
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

    private void writeToStream(byte result)
    {
        try
        {
            outputStream.write(result);
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
