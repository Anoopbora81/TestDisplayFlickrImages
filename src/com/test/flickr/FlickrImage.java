package com.test.flickr;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class FlickrImage implements Serializable{
	String Title;
	String Link;
	Bitmap FlickrBitmap;

	FlickrImage(String title, String link) {
		this.Title = title;
		FlickrBitmap = preloadBitmap(link);
	}

	private Bitmap preloadBitmap(String link) {

		ImageDimension dimension = new ImageDimension();
		Bitmap bm = null, bitmap = null;
		String FlickrPhotoPath = null;
		FlickrPhotoPath = link;
		URL FlickrPhotoUrl = null;
		try {
			FlickrPhotoUrl = new URL(FlickrPhotoPath);

			HttpURLConnection httpConnection = (HttpURLConnection) FlickrPhotoUrl
					.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.connect();
			InputStream inputStream = httpConnection.getInputStream();
			bm = BitmapFactory.decodeStream(inputStream);
			 dimension=calculateImageAspectRatio(bm.getWidth(),	 bm.getHeight(), 350,350);
			 bitmap = Bitmap.createScaledBitmap(bm,dimension.getDisplayWidth(),dimension.getDisplayHeight(), true);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bm;
	}

	public Bitmap getBitmap() {
		return FlickrBitmap;
	}
	
	 public ImageDimension calculateImageAspectRatio(double imgWidth,
				double imgHeight, double availableWidth, double availableHeight) {
			ImageDimension imageDimension = new ImageDimension();
			if (imgWidth > imgHeight) {
				availableHeight = (imgHeight / imgWidth) * availableWidth;
			} else if (imgHeight > imgWidth) {
				availableWidth = (imgWidth / imgHeight) * availableHeight;
			} else {
				if (availableWidth > availableHeight) {
					availableWidth = availableHeight;
				} else {
					availableHeight = availableWidth;
				}
			}
			imageDimension.setDisplayHeight((int) availableHeight);
			imageDimension.setDisplayWidth((int) availableWidth);
			return imageDimension;
		}
	
}