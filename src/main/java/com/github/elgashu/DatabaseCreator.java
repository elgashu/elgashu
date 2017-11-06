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
