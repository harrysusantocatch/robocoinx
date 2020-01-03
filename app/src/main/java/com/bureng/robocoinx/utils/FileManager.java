package com.bureng.robocoinx.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {

    private static FileManager Instance;
    public static FileManager getInstance(){
        if(Instance == null){
            Instance = new FileManager();
        }
        return Instance;
    }

    public boolean delete(Context context, String fileName){
        return context.deleteFile(fileName);
    }

    public String readFile(Context context, String fileName) {
        String content = "";
        try {
            FileInputStream fin = context.openFileInput(fileName);
            int c;
            while( (c = fin.read()) != -1){
                content = content + Character.toString((char)c);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return content;
    }

    public void writeFile(Context context, String fileName, String content){
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public void InsertOrUpdate(Context context, String filename, String content){
        if(fileExists(context, filename)){
            boolean success = delete(context, filename);
        }
        writeFile(context, filename, content);
    }

    public void appendLog(Exception ex)
    {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(folder.getAbsolutePath());
        File logFile = new File(dir, "robocoinxlog.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(sw.toString());
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void appendLog(String msg)
    {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(folder.getAbsolutePath());
        File logFile = new File(dir, "robocoinxlog.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(currentTime + " " + msg);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean canWriteOnExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
