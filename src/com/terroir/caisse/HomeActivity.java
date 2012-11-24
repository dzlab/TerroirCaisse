package com.terroir.caisse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.terroir.caisse.adapter.HomeAdapter;
import com.terroir.caisse.data.DBAdapter;
import com.terroir.caisse.data.Producer;
import com.terroir.caisse.helper.DistanceComparator;
import com.terroir.caisse.helper.OpenDataXmlParser;

public class HomeActivity extends Activity {
	
	String TAG = HomeActivity.class.getSimpleName();
	protected HomeAdapter adapter;
	protected ListView list;
	protected Context context;
	protected Location location;
	
	public static List<Producer> producers = null;
	public static Map<String, List<Producer>> categories = new HashMap<String, List<Producer>>();
	public static final String[] CATEGORIES = new String[] {
		"MIEL", "LAITIERS", "OLIVES", "BOULANGERIE", "FRUITS", "VIANDE", "AROME", "GOURMANDISES", "VOLAILLES", "PLANTES", "SEL", "SPIRITUEUX", "JUS"};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "HomeActivity lauched!");
        context = this;
        list = (ListView) HomeActivity.this.findViewById(R.id.lstProducers);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView l, View v, int position, long id) {
				Producer producer = (Producer) adapter.getItem(position);    			   
				Intent wake = new Intent(HomeActivity.this, CardActivity.class);				
			    wake.putExtra("raison_social", producer.raison_social);
			    wake.putExtra("address", producer.address);
			    wake.putExtra("code_postal", producer.code_postal);
			    wake.putExtra("distance", String.valueOf(producer.distance));
			    wake.putExtra("latitude", producer.latitude);
			    wake.putExtra("longitude", producer.longitude);
			    wake.putExtra("mail", producer.mail);
			    wake.putExtra("sous_type", producer.sous_type);
			    wake.putExtra("telephone", producer.telephone);
			    wake.putExtra("ville", producer.ville);
			    startActivity(wake);			    			   
			}
		});
        ImageButton btnMap = (ImageButton) findViewById(R.id.btnHomeMap);
        btnMap.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent wake = new Intent(HomeActivity.this, HomeMapActivity.class);				
				startActivity(wake);
			}
		});
        ImageView infoButton = (ImageView) findViewById(R.id.btnHomeInfo);
		infoButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {	
				LayoutInflater factory = HomeActivity.this.getLayoutInflater();
                final View alertDialogView = factory.inflate(R.layout.dialog_bdrt, null);
                AlertDialog.Builder adb = new AlertDialog.Builder(HomeActivity.this); 
                adb.setView(alertDialogView);
                adb.setTitle("Information fournis par la BDRT"); 
                ImageView logo = (ImageView) alertDialogView.findViewById(R.id.logo_bdrt);
                logo.setImageResource(R.drawable.bdrt_logotype_noir);	                
                adb.show();
			}
		});
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);				
		Criteria criteria = new Criteria();
	    String provider = lm.getBestProvider(criteria, false);
	    location = lm.getLastKnownLocation(provider);
                       
        String ws_url = "http://dataprovence.cloudapp.net:8080/v1/dataprovencetourisme/ProducteursDuTerroir/";
        new ProgressTask().execute(ws_url);
    }
    
    protected void load(String url) throws IOException, XmlPullParserException {
    	producers = null;
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	if(prefs.contains("initiated")){    		
    		Log.i(TAG, "Already initiated !");
    		DBAdapter db = new DBAdapter(context);
    		db.open();
    		producers = db.query();
    		db.close();
    	}else {
    		//Log.i(TAG, "First launch !");
    		OpenDataXmlParser parser = new OpenDataXmlParser(HomeActivity.this);
    		InputStream stream = parser.downloadUrl(url);
    		Log.i(TAG, "parsing xml stream "+url); 
    		producers = parser.parse(stream);
    		//Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    	    //editor.putBoolean("initiated", true);
    	    //editor.commit();
    	}
		DistanceComparator comparator = new DistanceComparator(location); 
		Collections.sort(producers, comparator);		
		adapter = new HomeAdapter(context, producers);		
		HomeActivity.this.runOnUiThread(new Runnable(){  		    
			@Override  
			public void run() {
				list.setAdapter(adapter);
		    }  		    
		});    
		for(Producer p: producers) {
			try {
				if(!categories.containsKey(p.sous_type)) {						
					categories.put(p.sous_type, new ArrayList<Producer>());
				}
				categories.get(p.sous_type).add(p);	
			}catch(Exception e) {
				e.printStackTrace();
			}			
		}
    }
   
    protected boolean isNetworkConnected() {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);    	
    	NetworkInfo ni = cm.getActiveNetworkInfo();    	
    	if (ni == null) {    	
    		// There are no active networks.    	   
    		return false;    	  
    	} else    	
    		return true;    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {    
/*        
        case R.id.map_option:
        	Intent wake = new Intent(HomeActivity.this, HomeMapActivity.class);
			startActivity(wake);
        	return true;
        
            case R.id.refresh_option:
            	DBAdapter db = new DBAdapter(context);
            	db.open();
            	db.drop();            	
            	return true;                      
*/            	
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
    
        public ProgressTask() {           
            dialog = new ProgressDialog(HomeActivity.this);
        }

        protected void onPreExecute() {
        	this.dialog.setTitle("TerroirCaisse");
        	this.dialog.setIcon(R.drawable.patch_by_bdrt1);
        	this.dialog.setMessage("Chargement en cours...");
        	//this.dialog.setProgressStyle(R.style.CustomDialogTheme);
        	this.dialog.setCancelable(false);
        	this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {                    
        	//setListAdapter(listAdapter);             
            if (dialog.isShowing()) {
            	dialog.dismiss();
            	dialog = null;
            }

            if (success) {
            //Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
            } else {
            	Toast.makeText(HomeActivity.this, "Erreur de connexion", Toast.LENGTH_LONG).show();
            }
    
        }

    
        protected Boolean doInBackground(final String... url) {        
        	try{        	            
        		load(url[0]);            
        		return true;         
        	} catch (Exception e){           
        		e.printStackTrace();            
        		return false;         
        	}      
        }
    }
    

}