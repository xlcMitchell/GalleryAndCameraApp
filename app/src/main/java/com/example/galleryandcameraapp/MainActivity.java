package com.example.galleryandcameraapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagePaths = new ArrayList<>();
        imagesRV = findViewById(R.id.galleryRecyclerView);
        prepareRecyclerView();
        requestPermissions();

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_REQUEST_CODE:
                //checking if pmissions are accepted
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