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
