package com.example.gallery_test2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.terroir.caisse.FullImage;
import com.terroir.caisse.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.view.View.OnClickListener;

	public class ImageAdapter extends BaseAdapter{
	
	Activity a;
	ArrayList<ImageData> data;
	
    public ImageAdapter(Activity a, ArrayList<ImageData> data)
    {
        super();
        this.a = a;
        this.data = data;
    }   
    @Override public int getCount() {

        return data.size();
    }

    @Override public ImageData getItem(int position) {

        return data.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = a.getLayoutInflater().inflate(R.layout.cell, null);

        ImageData imageData = getItem(position);
        String url = imageData.thumbnail;  
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        addImage(new ImageLoadingData(url, image));
        
		   convertView.setTag(imageData);
	       convertView.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
            	   ImageData imageData = (ImageData)v.getTag();
            	   openFullSizeImage(imageData);
               }
      });

        return convertView;
    }
    public static String IMAGE_URL_EXTRA = "IMAGE_URL_EXTRA";
    public void openFullSizeImage(ImageData imageData){
    	Log.i("DEBU LEX", imageData.picture);
//    	View convertView = a.getLayoutInflater().inflate(R.layout.activity_main, null);
//    	ImageSwitcher image = (ImageSwitcher) convertView.findViewById(R.id.ImageSwitcher01);
//    	image.setVisibility(View.VISIBLE);
//    	image.setY(2000.0);
//    	addImage(new ImageLoadingData(imageData.picture, image));
//    	image.setImageURI(Uri.parse(imageData.picture));
    	
    	Intent intent = new Intent(a, FullImage.class);
    	intent.putExtra(IMAGE_URL_EXTRA, imageData.picture);
    	a.startActivity(intent);
    }
    
	public static ArrayList<ImageLoadingData> imageDataArray = new ArrayList<ImageLoadingData>();
	public static Boolean isLoading = false;
   	static void addImage(ImageLoadingData imageLoadingData){
   		imageDataArray.add(imageLoadingData);
   		if (isLoading == false){
   			loadNextImage();
   			isLoading = true;
   		}
   	}
  	static void loadNextImage(){
    	Log.i("DEBU LEX", "loadNextImage "+imageDataArray.size());
		if (imageDataArray.size() == 0){
			isLoading = false;
		}
		else{
			ImageLoadingData imageLoadingData = imageDataArray.remove(0);
	        new LoadImage().execute(new ImageLoadingData(imageLoadingData.url, imageLoadingData.image));
		}
	}
	
 }