package com.example.robocoinx.logic;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
}
