package com.github.elgashu;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseCreator implements Closeable
{
    private final int indexInterval;
    private final OutputStream database;
    private final DataOutputStream index;

    private int length;
    private byte[] lastHash;

    public DatabaseCreator(int indexInterval, Path file) throws IOException
    {
        this.indexInterval = indexInterval;
        database = new BufferedOutputStream(Files.newOutputStream(file));
        index = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(Index.getIndexFile(file))));
    }

    public void add(byte[] hash) throws IOException
    {
        if (length == 0 || length % indexInterval == 0) {
            addToIndex(hash, length);
        }
        database.write(hash);
        length++;
        lastHash = hash;
    }

    private void addToIndex(byte[] hash, int position) throws IOException
    {
        index.write(hash);
        index.writeInt(position);
    }

    @Override
    public void close() throws IOException
    {
        addToIndex(lastHash, length - 1);
        Closables.tryClose(database, "database");
        Closables.tryClose(index, "index");
    }
}
