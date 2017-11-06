package com.github.elgashu;

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
