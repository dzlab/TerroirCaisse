package com.terroir.caisse;

import com.example.gallery_test2.ImageAdapter;
import com.example.gallery_test2.ImageLoadingData;
import com.example.gallery_test2.LoadImage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FullImage extends Activity {
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.full_image_display);
	        ImageView imageView = (ImageView) findViewById(R.id.image);
	        
	        Intent intent = getIntent();
	        String url = intent.getStringExtra(ImageAdapter.IMAGE_URL_EXTRA);
	        new LoadImage().execute(new ImageLoadingData(url, imageView));

	    
	       imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
         	   finish();
            }
	      });
	    }
}
