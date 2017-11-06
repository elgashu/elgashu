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

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.DatatypeConverter;

public class TestData
{
    public static final String BEFORE_A = "0123e72be0377ab0e899791a9cbf2f0613813c18";
    public static final String A = "0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33";
    public static final String BETWEEN_AB = "0da9901af6fe030198ef1737783e2048ee96da4a";
    public static final String B = "124b615c461871b4e67c6dc30607bbfa5db2431d";
    public static final String BETWEEN_BC = "341632bdac79327444791db6bb4551a94e1cd906";
    public static final String BETWEEN_BC_MISSING = "555aca05158d5d3a9f81bf18f4b4d36d141a99ad";
    public static final String C = "62cdb7020ff920e5aa642c3d4066950dd1f01f4d";
    public static final String BETWEEN_CD = "adec1de773ca60ac7cb42f2024132da14cc6718a";
    public static final String D = "ae2ad9454f3af7fcb18c83969f99b20a788eddd1";
    public static final String AFTER_D = "fe2b750ba9525d706add3140dfc954b5beb3ef7c";
    public static final int POSITION_A = 0;
    public static final int POSITION_B = 2;
    public static final int POSITION_C = 4;
    public static final int POSITION_D = 6;

    private Path indexFile;
    private Path dataFile;

    public Path getIndexFile() throws IOException
    {
        if (indexFile == null)
        {
            indexFile = Files.createTempFile(getClass().getCanonicalName(), "index");
            indexFile.toFile().deleteOnExit();

            try (DataOutputStream stream = new DataOutputStream(Files.newOutputStream(indexFile)))
            {
                writeIndexEntry(stream, A, POSITION_A);
                writeIndexEntry(stream, B, POSITION_B);
                writeIndexEntry(stream, C, POSITION_C);
                writeIndexEntry(stream, D, POSITION_D);
            }
        }
        return indexFile;
    }

    Object[][] getIndexBoundsTestData()
    {
        return new Object[][]{
            new Object[]{ BEFORE_A, POSITION_A, POSITION_A },
            new Object[]{ A, POSITION_A, POSITION_A },
            new Object[]{ BETWEEN_AB, POSITION_A, POSITION_B },
            new Object[]{ B, POSITION_B, POSITION_B },
            new Object[]{ BETWEEN_BC, POSITION_B, POSITION_C },
            new Object[]{ C, POSITION_C, POSITION_C },
            new Object[]{ BETWEEN_CD, POSITION_C, POSITION_D },
            new Object[]{ D, POSITION_D, POSITION_D },
            new Object[]{ AFTER_D, POSITION_D, POSITION_D }
        };
    }

    private void writeIndexEntry(DataOutputStream stream, String hash, int position) throws IOException
    {
        stream.write(fromHex(hash));
        stream.writeInt(position);
    }

    private byte[] fromHex(String hashString)
    {
        return DatatypeConverter.parseHexBinary(hashString);
    }

    public Path getDataFile() throws IOException
    {
        if (dataFile == null)
        {
            dataFile = Files.createTempFile(getClass().getCanonicalName(), "data");
            dataFile.toFile().deleteOnExit();

            try (DataOutputStream stream = new DataOutputStream(Files.newOutputStream(dataFile)))
            {
                writeHash(stream, A);
                writeHash(stream, BETWEEN_AB);
                writeHash(stream, B);
                writeHash(stream, BETWEEN_BC);
                writeHash(stream, C);
                writeHash(stream, BETWEEN_CD);
                writeHash(stream, D);
            }
        }
        return dataFile;
    }

    private void writeHash(DataOutputStream stream, String hashString) throws IOException
    {
        stream.write(fromHex(hashString));
    }

    Object[][] getExistingHashes()
    {
        return new Object[][]{
            new Object[]{ A },
            new Object[]{ BETWEEN_AB },
            new Object[]{ B },
            new Object[]{ BETWEEN_BC },
            new Object[]{ C },
            new Object[]{ BETWEEN_CD },
            new Object[]{ D }
        };
    }

    Object[][] getMissingHashes()
    {
        return new Object[][]{
            new Object[]{ BEFORE_A },
            new Object[]{ BETWEEN_BC_MISSING },
            new Object[]{ AFTER_D }
        };
    }
}
