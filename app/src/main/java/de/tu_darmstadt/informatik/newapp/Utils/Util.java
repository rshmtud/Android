package de.tu_darmstadt.informatik.newapp.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by manna on 24-01-2017.
 */

public class Util {

    //Logic for file copy referred from stackOverflow : manna
    public static void makeFileCopy(File source, File destinition) throws IOException
    {
        InputStream inpstr = new FileInputStream(source);
        // Rename new File for validation over URI on Clients : manna Dec 2016
        String name = destinition.toString().replaceAll("\\s","");
        OutputStream outstr = new FileOutputStream(name);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inpstr.read(buffer)) > 0)
        {
            outstr.write(buffer, 0, length);
        }
        inpstr.close();
        outstr.close();



    }

}
