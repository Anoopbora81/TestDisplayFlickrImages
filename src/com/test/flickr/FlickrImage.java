package com.test.flickr;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FlickrImage implements Serializable {
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
			// bm = decodeFile(inputStream);
			// bm = decodeSampledBitmapFromStream(inputStream);
			dimension = calculateImageAspectRatio(bm.getWidth(),
					bm.getHeight(), 100, 100);
			bitmap = Bitmap.createScaledBitmap(bm, dimension.getDisplayWidth(),
					dimension.getDisplayHeight(), true);
			dimension = null;
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

	public static Bitmap decodeSampledBitmapFromStream(InputStream is) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 100, 100);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(is, null, options); // decodeResource(res,
																// resId,
																// options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(InputStream inputStream) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			Bitmap bm = BitmapFactory.decodeStream(inputStream, null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 100;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inJustDecodeBounds = false;
			o2.inSampleSize = scale;

			bm = BitmapFactory.decodeStream(inputStream, null, o2);
			return bm;
		} catch (Exception e) {
		}
		return null;
	}

}