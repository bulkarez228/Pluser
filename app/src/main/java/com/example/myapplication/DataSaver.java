package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DataSaver{
    private static final String TAG = "Data saver";
    private static final String fileName = "classes";

    public static ArrayList<Classes> getData(Context context){
        ArrayList<Classes> classes=new ArrayList<>();
        try {
            if (new File(context.getFilesDir(), "classes").exists()) {
                FileInputStream fileInputStream = context.openFileInput("classes");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                String jsonString = stringBuilder.toString();

                classes = new ObjectMapper().readValue(jsonString, new TypeReference<ArrayList<Classes>>(){});
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return classes;
    }

    public static void SaveData(Context context, ArrayList<Classes> stations) {
        try {
            if (! new File(context.getFilesDir(), "classes").exists()) new File(context.getFilesDir(), "classes").createNewFile();

            String jsonString = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(stations);
            FileOutputStream fileOutputStream;
            fileOutputStream = context.openFileOutput("classes", Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            Log.d(TAG, jsonString);
            fileOutputStream.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}