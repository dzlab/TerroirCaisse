package com.terroir.caisse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gallery_test2.ImageAdapter;
import com.example.gallery_test2.ImageData;
import com.terroir.caisse.data.Producer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class CardActivity extends Activity {
	
	public static String TAG = CardActivity.class.getSimpleName();

	private static String INSTAGRAM_TAGS = "https://api.instagram.com/v1/tags/";
	
	private static String INSTAGRAM_ACCESS_TOKEN = "/media/recent?access_token=49812874.f59def8.7faedd01ba4845ffa9cee60a7d369f02";
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_card);
		final Producer producer = new Producer();		
			
		try {
			Intent intent = getIntent();
			String name = intent.getStringExtra("raison_social");
			
			if (name == null)
			{
				name = "Boulanger";
			}
			producer.raison_social = name;
			TextView text = (TextView) findViewById(R.id.txtViewName);
			text.setText(name);

			
			String mail = intent.getStringExtra("mail");
			producer.mail = mail;
			TextView mailText = (TextView) findViewById(R.id.txtViewMail);
			mailText.setText(mail);
			
			
			
			String website = intent.getStringExtra("addresse_web");
			producer.addresse_web = website;
			TextView websiteText = (TextView) findViewById(R.id.txtViewWebSite);
			websiteText.setText(website);
			
			
			
			String phone = intent.getStringExtra("telephone");
			
			if (phone == null)
			{
				phone = "0346782132";
			}
			producer.telephone = phone;
			TextView phoneText = (TextView) findViewById(R.id.txtPhone);
			phoneText.setText(phone);
			
			ImageView imgView = (ImageView) findViewById(R.id.imgPhone);
			imgView.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// listen for phone state changes to restart the app when the initiated call ends
					EndCallListener callListener = new EndCallListener();
					TelephonyManager mTM = (TelephonyManager)CardActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
					mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
					

					Intent intent = getIntent();
					String phone = intent.getStringExtra("telephone");
					
				    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));			
				    CardActivity.this.startActivity(callIntent);
				}
			});	
			
			String hashTag = getHashTag();
			
			InstagramLoader loader = (InstagramLoader) new InstagramLoader().execute(INSTAGRAM_TAGS + hashTag + INSTAGRAM_ACCESS_TOKEN);
					
			producer.latitude = Double.parseDouble(intent.getStringExtra("latitude"));
			producer.longitude = Double.parseDouble(intent.getStringExtra("longitude"));
			ImageView bookmarkButton = (ImageView) findViewById(R.id.bookmarkButton);
			bookmarkButton.setOnClickListener(new OnClickListener() {						
				@Override				
				public void onClick(View v) {				
					FavorisActivity.add(producer);					
					Log.i(TAG, "Bookmarking this producer: "+producer);
				}				
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        ImageView sharingButton = (ImageView) findViewById(R.id.sharingButton);

        
        sharingButton.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
            shareIt();
          }
        });
        
        Button tweetButton = (Button) findViewById(R.id.tweetButton);
        
        tweetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              tweetIt();
            }
          });
                  
        
	}
	
	private String getHashTag(){
		
		Intent intent = getIntent();
		
		String name = intent.getStringExtra("raison_social");
		
		String escapedName = "";
		if (name == null) {
			escapedName = "nofilter";
		}
		else {
			escapedName = name.replace(" ", "_");
		}
		escapedName = "#"+escapedName;
		return escapedName;
	}
	
	public Intent findTwitterClient() {
		final String[] twitterApps = {
				// package // name - nb installs (thousands)
				"com.twitter.android", // official - 10 000
				"com.twidroid", // twidroyd - 5 000
				"com.handmark.tweetcaster", // Tweecaster - 5 000
				"com.thedeck.android" // TweetDeck - 5 000 
				};
		Intent tweetIntent = new Intent();
		tweetIntent.setType("text/plain");
		final PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		for (int i = 0; i < twitterApps.length; i++) {
			for (ResolveInfo resolveInfo : list) {
				String p = resolveInfo.activityInfo.packageName;
				if (p != null && p.startsWith(twitterApps[i])) {
					tweetIntent.setPackage(p);
					return tweetIntent;
				}
			}
		}
		return null;
	}
	
	private void onInstagramLoaded(String result) {
		
		ArrayList<ImageData> imageArray = new ArrayList<ImageData>();
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			
			JSONArray dataArray = jsonObject.getJSONArray("data");
			int length = dataArray.length();
			for (int i = 0; i < length; i++)
			{
				ImageData instagramVO = new ImageData();
				
				JSONObject data = dataArray.getJSONObject(i);
				try{
					
					if (data.has("caption") == true)
					{
						JSONObject caption = data.getJSONObject("caption");
						instagramVO.caption = caption.getString("text");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				try{
					
					if (data.has("images") == true)
					{
						JSONObject images = data.getJSONObject("images");
						instagramVO.thumbnail = images.getJSONObject("thumbnail").getString("url");
						instagramVO.picture = images.getJSONObject("standard_resolution").getString("url");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				imageArray.add(instagramVO);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 GridView gridView = (GridView) findViewById(R.id.gridView1);
	        
			ImageAdapter adapter = new ImageAdapter(this, imageArray);
			gridView.setAdapter(adapter);
		
	}
	
	private void tweetIt(){
		 Intent sharingIntent = findTwitterClient();
        sharingIntent.setType("text/plain");
        
        
        String shareBody = getHashTag();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	private void shareIt(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
	
	private class InstagramVO {
		public String thumbnail;
		public String picture;
		public String caption;
	}
	
	private class EndCallListener extends PhoneStateListener {
	    @Override
	    public void onCallStateChanged(int state, String incomingNumber) {
	        if(TelephonyManager.CALL_STATE_RINGING == state) {
	            Log.i(TAG, "RINGING, number: " + incomingNumber);
	        }
	        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
	            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
	            Log.i(TAG, "OFFHOOK");
	        }
	        if(TelephonyManager.CALL_STATE_IDLE == state) {
	            //when this state occurs, and your flag is set, restart your app
	            Log.i(TAG, "IDLE");
	        }
	    }
	}
	
	
	private class InstagramLoader extends AsyncTask<String, Void, String> {
		
		@Override
	    protected String doInBackground(String... url) {
	          
	        // params comes from the execute() call: params[0] is the url.
	        try {
	            return downloadUrl(url[0]);
	        } catch (IOException e) {
	            return "Unable to retrieve web page. URL may be invalid.";
	        }
	    }
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(String result) {
	    	onInstagramLoaded(result);
	   }
		
		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private String downloadUrl(String urlAsString) throws IOException {
		    InputStream is = null;

		    try {
		    	URL url = new URL(urlAsString);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("GET");
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		       
		        is = conn.getInputStream();

		        // Convert the InputStream into a string
		        String contentAsString = convertStreamToString(is);
		        return contentAsString;
		        
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } finally {
		        if (is != null) {
		            is.close();
		        } 
		   }
		}
		public String convertStreamToString(InputStream is)
	            throws IOException {
	        //
	        // To convert the InputStream to String we use the
	        // Reader.read(char[] buffer) method. We iterate until the
	        // Reader return -1 which means there's no more data to
	        // read. We use the StringWriter class to produce the string.
	        //
	        if (is != null) {
	            Writer writer = new StringWriter();

	            char[] buffer = new char[1024];
	            try {
	                Reader reader = new BufferedReader(
	                        new InputStreamReader(is, "UTF-8"));
	                int n;
	                while ((n = reader.read(buffer)) != -1) {
	                    writer.write(buffer, 0, n);
	                }
	            } finally {
	                is.close();
	            }
	            return writer.toString();
	        } else {        
	            return "";
	        }
	    }
	}

}
