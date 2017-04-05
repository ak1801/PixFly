package com.pml.pixfly.util;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aksmahaj.
 */
public class FileOperationsUtil {

    /*
   Reads data from file
   Returns : ArrayList
    */
    public static ArrayList<String> readFromFile(Context context, String FILE_NAME) {

        ArrayList<String> listfromFile = new ArrayList<String>();
        FileInputStream fileIn = null;
        InputStreamReader InputRead = null;
        BufferedReader br = null;

        try {
            File file = context.getFileStreamPath(FILE_NAME);
            if (file == null || !file.exists()) {
                return null;
            } else {
                fileIn = context.openFileInput(FILE_NAME);
                String sCurrentLine;

                br = new BufferedReader(new InputStreamReader(fileIn));
                while ((sCurrentLine = br.readLine()) != null) {
                    listfromFile.add(sCurrentLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return listfromFile;
    }

    /*
    Saves data to a File
     */
    public static void writeToFile(Context context, String txt, String file, int mode) {
        Toast.makeText(context, txt,
                Toast.LENGTH_SHORT).show();

        try {
            FileOutputStream fileOutput = context.openFileOutput(file, mode);
            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(fileOutput));

            outputWriter.write(txt);
            outputWriter.newLine();

            outputWriter.close();
            fileOutput.close();
            /*Toast.makeText(context, "Location saved successfully!",
                    Toast.LENGTH_SHORT).show(); */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
