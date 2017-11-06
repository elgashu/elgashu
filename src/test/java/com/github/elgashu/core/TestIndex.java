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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestIndex
{
    private TestData testData = new TestData();
    private Index index;

    @BeforeMethod
    public void setUp() throws IOException
    {
        index = new Index(testData.getIndexFile());
    }

    @Test(dataProvider = "getBoundsTestData")
    public void testGetBounds(String hashString, int lower, int higher) throws IOException
    {
        Index.Bounds bounds = index.getBounds(fromHex(hashString));
        assertThat(bounds.getLower()).isEqualTo(lower);
        assertThat(bounds.getHigher()).isEqualTo(higher);
    }

    private byte[] fromHex(String hashString)
    {
        return DatatypeConverter.parseHexBinary(hashString);
    }

    @DataProvider
    private Object[][] getBoundsTestData()
    {
        return testData.getIndexBoundsTestData();
    }
}
