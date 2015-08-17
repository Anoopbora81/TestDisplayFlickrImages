package com.test.flickr;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import com.test.flickr.StackOverflowXmlParser.Entry;
import com.test.flickr.StackOverflowXmlParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AndroidFlickrActivity extends Activity implements OnClickListener {

	public static ArrayList<FlickrImage> myFlickrImg = new ArrayList<FlickrImage>();
	EditText searchText;
	Button searchButton;
	public static GridView gridView;
	Bitmap bmFlickr;
	ProgressBar pb;
	private EditText tagsEditView;
	List<Entry> entries;
	private String tagsData;
	final static String urlString = "https://api.flickr.com/services/feeds/photos_public.gne";// this.getString(R.string.flickr_url);
	private static boolean resetTags = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainsearch);
		searchButton = (Button) findViewById(R.id.searchbutton);
		gridView = (GridView) findViewById(R.id.gridView);
		tagsEditView = (EditText) findViewById(R.id.tagsEditView);

		initializeComponents();
		searchButton.setOnClickListener((OnClickListener) this);
		// Flickr flr =Flickr.get();
		// User user = flr.findByUserName("Anoop");
		// Log.i("User","User= "+user);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchbutton:
			resetTags = true;
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(tagsEditView.getWindowToken(), 0);

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			if (nInfo != null && nInfo.isAvailable() == true) {

				tagsData = null;
				entries = null;
				tagsData = (String) tagsEditView.getText().toString();

				if (tagsData != null && tagsData.trim().length() > 0) {
					tagsData = "?tags=" + tagsData;
				} else {
					tagsData = "";
				}
				Log.i("Anoop", "urlString= " + urlString + tagsData);
				Toast.makeText(getBaseContext(),
						"urlString= " + urlString + tagsData,
						Toast.LENGTH_SHORT).show();
				myFlickrImg.clear();

				(new SearchAsyncTask()).execute();

			} else {
				Toast.makeText(v.getContext(), "No Network Connection",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void initializeComponents() {
		float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, getResources().getDisplayMetrics());
		gridView.setNumColumns(4);
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

	private void parseXml(InputStream is) {

		StackOverflowXmlParser stackOverflowXmlParser = new StackOverflowXmlParser();
		try {
			entries = stackOverflowXmlParser.parse(is);
			stackOverflowXmlParser = null;
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
	}

	class SearchAsyncTask extends
			AsyncTask<Void, ArrayList<FlickrImage>, ArrayList<FlickrImage>> {
		private ProgressDialog progressDialog;
		private Integer totalCount, currentIndex;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			searchButton.setEnabled(false);
			progressDialog = null;
			progressDialog = new ProgressDialog(AndroidFlickrActivity.this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog
					.setMessage("Loading images from Flickr. Please wait...");
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(
				/* FlickrImage[]... values */ArrayList<FlickrImage>... values) {
			super.onProgressUpdate(values);
			// progressDialog.setMessage(String.format(
			// "Loading images from Flickr %s/%s. Please wait...",
			// values[0], values[1]));

			gridView.setAdapter(new FlickrAdapter(AndroidFlickrActivity.this,
					values[0]));
			gridView.invalidate();
			progressDialog.dismiss();
			searchButton.setEnabled(true);
		}

		@Override
		protected ArrayList<FlickrImage> doInBackground(Void... params) {

			try {
				// FlickrImage[] flickrImage = null;
				InputStream inputStream = downloadUrl(urlString + tagsData);
				parseXml(inputStream);
				FlickrImage flcrImg;
				int i = 0;
				currentIndex = 0;
				for (Entry entry : entries) {

					if (resetTags == true && currentIndex > 0) {
						break;
					} else {
						resetTags = false;
					}
					currentIndex++;
					flcrImg = new FlickrImage(entry.title, entry.link);
					myFlickrImg.add(flcrImg);
					publishProgress(myFlickrImg);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return myFlickrImg;
		}

		@Override
		protected void onPostExecute(ArrayList<FlickrImage> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			searchButton.setEnabled(true);
			progressDialog.dismiss();
			currentIndex = 0;
			totalCount = 0;
			// gridView.setAdapter(new FlickrAdapter(AndroidFlickrActivity.this,
			// result));
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
