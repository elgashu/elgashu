package com.github.elgashu;

import java.io.Closeable;
import java.io.IOException;

public final class Closables
{
    private Closables()
    {
    }

    public static void tryClose(Closeable closeable, String name)
    {
        if (closeable != null) {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                System.err.print("Could not close " + name);
                e.printStackTrace();
            }
        }
    }
}
