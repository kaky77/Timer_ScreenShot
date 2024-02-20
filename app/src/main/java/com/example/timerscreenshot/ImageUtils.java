package com.example.timerscreenshot;

import android.content.Context;

import java.io.File;

public class ImageUtils {

    public static void deleteImageFolder(Context context, String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            deleteRecursive(folder);
        }
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

}
