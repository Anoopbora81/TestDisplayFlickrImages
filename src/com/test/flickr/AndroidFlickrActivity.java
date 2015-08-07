package com.test.flickr;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.test.flickr.StackOverflowXmlParser.Entry;
import com.test.flickr.StackOverflowXmlParser;
//import com.test.flickr.networkusage.StackOverflowXmlParser.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class AndroidFlickrActivity extends Activity implements OnClickListener {

	
	private EditText searchText;
	private Button searchButton;
	private GridView gridView;
	private Bitmap bmFlickr;
	public static FlickrImage[] myFlickrImage;

	// ** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainsearch);
		searchButton = (Button) findViewById(R.id.searchbutton);
		gridView = (GridView) findViewById(R.id.gridView);
		initializeComponents();
		searchButton.setOnClickListener((OnClickListener) this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.searchbutton:
			myFlickrImage = null;
			(new SearchAsyncTask()).execute();
		}
	}

	private void initializeComponents() {
		float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, getResources()
						.getDisplayMetrics());
		gridView.setNumColumns(2);
		gridView.setPadding((int) spacing, (int) spacing, (int) spacing,
				(int) spacing);
		gridView.setVerticalSpacing((int) spacing);
		gridView.setHorizontalSpacing((int) spacing);
	}

	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(30000 /* milliseconds */);
		conn.setConnectTimeout(60000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
	}

	private List<Entry> parseXml(InputStream is) {

		List<Entry> entries = null;
		StringBuilder htmlString = new StringBuilder();
		StackOverflowXmlParser stackOverflowXmlParser = new StackOverflowXmlParser();
		try {
			// stream = downloadUrl(urlString);
			entries = stackOverflowXmlParser.parse(is);
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return entries;
	}

	class SearchAsyncTask extends AsyncTask<Void, Integer, FlickrImage[]> {
		private ProgressDialog progressDialog;
		private Integer totalCount, currentIndex;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(AndroidFlickrActivity.this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog
					.setMessage("Loading images from Flickr. Please wait...");
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressDialog.setMessage(String.format(
					"Loading images from Flickr %s/%s. Please wait...",
					values[0], values[1]));
		}

		@Override
		protected FlickrImage[] doInBackground(Void... params) {

			String urlString = "https://api.flickr.com/services/feeds/photos_public.gne";
			try {
				// FlickrImage[] flickrImage = null;
				InputStream inputStream = downloadUrl(urlString);
				List<Entry> entries = parseXml(inputStream);
				myFlickrImage = new FlickrImage[entries.size()];
				int i = 0;
				currentIndex = 0;
				totalCount = myFlickrImage.length;
				for (Entry entry : entries) {
					currentIndex++;
					myFlickrImage[i++] = new FlickrImage(entry.title, entry.link);
					
					publishProgress(currentIndex, totalCount);
//					if (i == 5) {
//						break;
//					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return myFlickrImage;
		}

		@Override
		protected void onPostExecute(FlickrImage[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			currentIndex = 0;
			totalCount = 0;
			gridView.setAdapter(new FlickrAdapter(AndroidFlickrActivity.this,
					myFlickrImage));
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			super.onKeyDown(keyCode, event);
			this.moveTaskToBack(true);
			return false;
		default:
			super.onKeyDown(keyCode, event);
			return false;
		}
	}

	

}
