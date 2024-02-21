package com.example.timerscreenshot;

import static android.content.ContentValues.TAG;
import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {


    private Screenshot screenshot = null;

    TextView textView;
    ConstraintLayout container;
    //ImageView image;

    String[] required_permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
    boolean is_storage_image_permitted = false;

    //Date date = new Date();
    //String folderDate = DateFormat.getDateTimeInstance().format(date);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container=(ConstraintLayout) findViewById(R.id.container);
        //image=(ImageView) findViewById(R.id.imageView);
        // for permission related to android 13
        if (!is_storage_image_permitted){
            requestPermissionStorageImages();
        }
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.MANAGE_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        //Timer();
        //RunTask();


        ContentResolver contentResolver = getContentResolver();
        /*screenshot = new Screenshot();

        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Bitmap screencapture =  screenshot.captureScreenShot(container);
                //storeImage(screenshot);
                String filename = screenshot.storeImage(screencapture,contentResolver);
                Log.d("filename source",filename);
                screenshot.showToast(getApplicationContext());

                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnableCode);*/

        //String folderpath = Environment.DIRECTORY_PICTURES+ File.separator+"BackupImage_2024-02-20";
       // File picturesDirectory = new File("/storage/emulated/0/Pictures/BackupImage_2024-02-20");

        //deleteImagesInFolder(contentResolver,"/storage/emulated/0/Pictures/BackupImage_2024-02-20");

        boolean deleted = deleteEmptyDirectory("/storage/emulated/0/Pictures/BackupImage_2024-02-20");
        if (deleted) {
            // Le répertoire vide a été supprimé avec succès
            Log.d(TAG,"Le répertoire vide a été supprimé avec succès");
        } else {
            // Le répertoire n'est pas vide ou n'existe pas
            Log.d(TAG,"Le répertoire n'est pas vide ou n'existe pas");
        }

    }


    public static void deleteImagesInFolder(ContentResolver contentResolver, String folderPath) {
        // 1. Récupérer l'URI du dossier
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // 2. Sélectionner toutes les images dans ce dossier
        String[] projection = { MediaStore.Images.Media._ID };
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = { folderPath + "%" };


        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 3. Supprimer chaque image
                @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                Uri contentUri = ContentUris.withAppendedId(uri, id);
                contentResolver.delete(contentUri, null, null);
            }
            cursor.close();
        }

        // 4. Supprimer le dossier lui-même
        contentResolver.delete(uri, MediaStore.Images.Media.DATA + "=?", new String[]{folderPath});
    }

    public static boolean deleteEmptyDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // Vérifie si le répertoire existe et est vide
        if (directory.exists() && directory.isDirectory() && directory.list().length == 0) {
            return directory.delete();
        } else {
            return false; // Le répertoire n'est pas vide ou n'existe pas
        }
    }


    public  void Timer(){
        textView = findViewById(R.id.textView);
        long duration = TimeUnit.MINUTES.toMillis(5);
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText("Timer: " + millisUntilFinished/1000);

            }
            @Override
            public void onFinish() {
                textView.setText("Done");
            }
        }.start();
    }

    private void RunTask() {
        MyScreenTask myToast = new MyScreenTask();
        myToast.execute();
    }
    @SuppressLint("StaticFieldLeak")
    private class MyScreenTask extends AsyncTask<String, Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            // Créez l'objet Handler (sur le thread principal par défaut)
            final Handler handler = new Handler();
            //Définir le bloc de code à exécuter
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    //Faites quelque chose ici sur le fil principal
                    Bitmap screenshot =  captureScreenShot(container);  //screenShotDevice();
                    //image.setImageBitmap(screenshot);
                    storeImage(screenshot);
                    //Répétez ceci avec le même bloc de code exécutable encore 3 secondes
                    //ceci fait référence à l'objet Runnable
                    handler.postDelayed(this, 3000);
                }
            };
            handler.post(runnableCode);
        }
    }


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


    // step 2 Store Image
    public void storeImage(Bitmap bitmap){
        OutputStream outfile;
        try {

            // current Date
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss",now);
            LocalDate dateFolder = LocalDate.now();

            // scoped storage is support after Q
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){

                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_" + now + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, DIRECTORY_PICTURES+ File.separator+"BackupImage_"+dateFolder);
                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

                outfile = contentResolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outfile);
                Objects.requireNonNull(outfile);

                Toast.makeText(this,"Image is saved",Toast.LENGTH_SHORT).show();

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }//END storeImageAsJPEGandShare


    public void requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,required_permissions[0])== PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,required_permissions[0] + "Granted");
            is_storage_image_permitted = true;
        } else{
            // new android 13 code after onActivityResult is deprecated, now ActivityResultLauncher...
            request_permission_launcher_storage_images.launch(required_permissions[0]);
        }
    } // END requestPermissionStorageImages

    private final ActivityResultLauncher<String> request_permission_launcher_storage_images =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted ->{
                        if(isGranted){
                            Log.d(TAG,required_permissions[0] + " Granted");
                            is_storage_image_permitted = true;
                        } else {
                            Log.d(TAG,required_permissions[0] + "Not Granted");
                            is_storage_image_permitted = false;
                        }
                    });

}// END MAINActivity