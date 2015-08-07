package com.test.flickr;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.test.flickr.FlickrImage;
public class FlickrAdapter extends BaseAdapter {
	private Context context;
	private FlickrImage[] FlickrAdapterImage;;
	
	FlickrAdapter(Context c) {
		context = c;
	}


	public FlickrAdapter(Context c, FlickrImage[] fImage) {
		context = c;
		FlickrAdapterImage = fImage;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return FlickrAdapterImage.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return FlickrAdapterImage[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView image;
		if (convertView == null) {
			image = new ImageView(context);
			image.setLayoutParams(new GridView.LayoutParams(350,350));
				//	LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			image.setPadding(8, 8, 8, 8);
		} else {
			image = (ImageView) convertView;
		}

		image.setImageBitmap(FlickrAdapterImage[position].getBitmap());
		image.setOnClickListener(new ImageGridViewCellOnClickListener(position));
		return image;
	}
	
	 class ImageGridViewCellOnClickListener implements View.OnClickListener {
	        private int position;

	        public ImageGridViewCellOnClickListener(int position) {
	            this.position = position;
	        }

	        @Override
	        public void onClick(View v) {
	        	
	            Intent intent = new Intent(context, ImageFullScreenActivity.class);
	            intent.putExtra("Position", position);
	           // intent.putExtra("selectedImage", AndroidFlickrActivity.myFlickrImage[position]);
	            context.startActivity(intent);
	        }
	    }
	 
	 

}
