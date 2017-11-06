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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestLookup
{
    private TestData testData = new TestData();
    private Lookup lookup;

    @BeforeMethod
    public void setUp() throws IOException
    {
        lookup = new Lookup(testData.getDataFile(), testData.getIndexFile());
    }

    @AfterMethod
    public void tearDown() throws IOException
    {
        lookup.close();
    }

    @Test(dataProvider = "existingHashes")
    public void testExistingHashes(String hashString) throws IOException
    {
        boolean result = lookup.lookup(hashString);
        assertThat(result).isTrue();
    }

    @DataProvider
    private Object[][] existingHashes()
    {
        return testData.getExistingHashes();
    }

    @Test(dataProvider = "missingHashes")
    public void testMissingHashes(String hashString) throws IOException
    {
        boolean result = lookup.lookup(hashString);
        assertThat(result).isFalse();
    }

    @DataProvider
    private Object[][] missingHashes()
    {
        return testData.getMissingHashes();
    }
}
