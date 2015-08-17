package com.test.flickr;

import java.util.ArrayList;

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
//	private FlickrImage[] FlickrAdapterImage;
	private ArrayList<FlickrImage> FlickrAdapterImage;;
	
	FlickrAdapter(Context c) {
		context = c;
	}


	public FlickrAdapter(Context c, ArrayList<FlickrImage> fImage) {
		context = c;
		FlickrAdapterImage = fImage;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return FlickrAdapterImage.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return FlickrAdapterImage.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	/**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<FlickrImage> mGridData) {
        this.FlickrAdapterImage = mGridData;
        notifyDataSetChanged();
    }


	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView image;
		if (convertView == null) {
			image = new ImageView(context);
			image.setLayoutParams(new GridView.LayoutParams(100,100));
				//	LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			image.setPadding(5, 5, 5, 5);
		} else {
			image = (ImageView) convertView;
		}

		image.setImageBitmap(FlickrAdapterImage.get(position).getBitmap());
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
