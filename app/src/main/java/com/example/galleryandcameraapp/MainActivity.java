package com.example.galleryandcameraapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> imagePaths; //File path for each photo in media store
    private final int PERMISSION_REQUEST_CODE = 200; //Identifies permission request made to system
    private RecyclerView imagesRV; //recycler view variable
    private RecyclerViewAdapter imageRVAdapter; //recyclerview adapter
    private int image_number = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = findViewById(R.id.cameraButton);
        imagePaths = new ArrayList<>();
        imagesRV = findViewById(R.id.galleryRecyclerView);

        ActivityResultLauncher<Intent> takePhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        Bundle bundle = result.getData().getExtras();
                        Bitmap image = (Bitmap) bundle.get("data");
                        Toast.makeText(getApplicationContext(), "Image recieved", Toast.LENGTH_SHORT).show();
                        MediaStore.Images.Media.insertImage(getContentResolver(),image,
                                "imag_" + image_number++,"image_captured");
                        imagePaths.clear();
                        getImagePath();
                    }
                }
        );

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.launch(intent);
            }
        });


        prepareRecyclerView();
        requestPermissions();

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_REQUEST_CODE:
                //checking if permissions are accepted
                if(grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        getImagePath();
                    }else{
                        Toast.makeText(this,"Permissions denied, Permissions are required to use the app",
                                Toast.LENGTH_SHORT).show();
                                 finish();
                    }
                }
                break;
        }
    }

    private void prepareRecyclerView(){
        imageRVAdapter = new RecyclerViewAdapter(MainActivity.this,imagePaths);
        GridLayoutManager manager = new GridLayoutManager(MainActivity.this,2);
        imagesRV.setLayoutManager(manager);
        imagesRV.setAdapter(imageRVAdapter);
    }

    private void requestPermissions(){
        if(checkPermission()){
            getImagePath();
        }else{
            requestPermission();
        }
    }

    private boolean checkPermission(){
        int result = checkSelfPermission(READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        requestPermissions(new String[]{READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }

    private void getImagePath(){
        final String [] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,null,null,orderBy);
        int count = cursor.getCount();
        Log.d("IMAGESDEBUG",String.valueOf(count));
        for(int i=0; i < count; i++){
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imagePaths.add(cursor.getString(dataColumnIndex));

        }
        imageRVAdapter.notifyDataSetChanged();
        cursor.close();
    }

}