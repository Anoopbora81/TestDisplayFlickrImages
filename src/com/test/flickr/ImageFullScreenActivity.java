package com.test.flickr;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.flickr.AndroidFlickrActivity;
import com.test.flickr.FlickrImage;
import com.test.flickr.FlickrAdapter;

	public class ImageFullScreenActivity extends Activity{
	   	  ImageView imgView;
	   	  TextView txtView;
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.imagefullview);
	        Intent intent = getIntent();
	        int position = intent.getIntExtra("Position", 0);
	      //  FlickrImage flickrImage= (FlickrImage)getIntent().getSerializableExtra("selectedImage");
	        txtView = (TextView)findViewById(R.id.imageTitle);
	        txtView.setText("Title: "+AndroidFlickrActivity.myFlickrImage[position].Title);
	        imgView = (ImageView)findViewById(R.id.image_view_fullscreen);
	        Bitmap bmp = AndroidFlickrActivity.myFlickrImage[position].getBitmap();
	        imgView.setImageBitmap(bmp);
	       // FlickrAdapter mediumViewAdapter = new FlickrAdapter(ImageFullScreenActivity.this, AndroidFlickrActivity.myFlickrImage);
			//((ViewPager) imgView).setAdapter(mediumViewAdapter);
	    }
	    
	}
