package com.example.galleryandcameraapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

//inner classes can be used to group classes that belong together
//this makes the code more readable and maintainable
//inner classes can be private or protected

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    private final Context context;
    private final ArrayList<String> imagePathArrayList;

    public RecyclerViewAdapter(Context context,ArrayList<String>imagePathArrayList){
        this.context = context;
        this.imagePathArrayList = imagePathArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_rv_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        File imgFile = new File(imagePathArrayList.get(position));
        if(imgFile.exists()){
            Log.d("IMAGEEXISTS","YESITDOES");
            Picasso.get().load(imgFile)
                    .resize(200,200)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.test_image)
                    .into(holder.imageIV,
                    new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("CameraAppDebug", "Image loaded: " + imagePathArrayList.get(position));
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("CameraAppDebug", "Failed to load: " + imagePathArrayList.get(position), e);
                        }
                    });
        }



        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ImageDetailActivity.class);
                intent.putExtra("imgPath",imagePathArrayList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imagePathArrayList.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageIV;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIV = itemView.findViewById(R.id.photoImageView);
        }
    }

}
