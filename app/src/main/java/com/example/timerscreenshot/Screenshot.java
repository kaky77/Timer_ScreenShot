package com.example.timerscreenshot;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Screenshot {


    // step 1 take capture of screen
    public Bitmap captureScreenShot(View view){

        Bitmap returnBitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitmap);
        Drawable bgdrawable = view.getBackground();
        if (bgdrawable!=null)
            bgdrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnBitmap;
    }//END captureScreenShot

    // step 2 store Image
    public String storeImage(Bitmap bitmap,ContentResolver contentResolver){
        OutputStream outst;
        try {

            // current Date
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss",now);
            LocalDate dateFolder = LocalDate.now();

            // scoped storage is support after Q
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){

                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_" + now + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"BackupImage_"+dateFolder);
                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

                outst = contentResolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outst);
                Objects.requireNonNull(outst);

                return ("BackupImage_"+dateFolder);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }//END storeImageAsJPEGandShare

    public void showToast(Context context) {
        Toast.makeText(context, "Image is saved", Toast.LENGTH_SHORT).show();
    } // END showToast


} // END Screenshot
