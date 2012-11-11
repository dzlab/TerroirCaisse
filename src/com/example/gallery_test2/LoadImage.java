package com.example.gallery_test2;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;



public class LoadImage extends AsyncTask<ImageLoadingData, Void, Bitmap> {
	ImageLoadingData imageLoadingData;

	@Override
    protected Bitmap doInBackground(ImageLoadingData... imageLoadingDataArray) {
    	this.imageLoadingData = imageLoadingDataArray[0];  
    	String url = imageLoadingDataArray[0].url;
   	 Log.d("BEFORE LOAD IMAGE URL", url);
    	Bitmap res = null;
		   try{
		        URL newurl = new URL(url); 
		        res = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
		        
		   } catch (IOException e) {
		       Log.e("DEBUG", "Could not load Bitmap from: " + url);
		   } 
		   finally {
		   }
       Log.d("DEBUG BACKGROUND", String.valueOf(res) + " - "+ imageLoadingData.url);
      return res;
    }
	@Override
    protected void onPostExecute(Bitmap result) {
    	 super.onPostExecute(result);
    	 Log.d("DEBUG IMAGE", "onPostExec"+String.valueOf(result) + " - "+ imageLoadingData.url);
    	imageLoadingData.image.setImageBitmap(result); 
    	ImageAdapter.loadNextImage();
    }
}

