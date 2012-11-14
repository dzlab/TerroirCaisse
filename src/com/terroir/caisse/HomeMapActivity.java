package com.terroir.caisse;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.terroir.caisse.data.Category;
import com.terroir.caisse.data.Producer;

public class HomeMapActivity extends MapActivity implements LocationListener {
	
	public static final String TAG = HomeMapActivity.class.getSimpleName();

	MapView			maMap			= null;
	MapController	monControler	= null;
	Location        monLocation     = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map_home);
        
        maMap = (MapView)findViewById(R.id.myGmap);
        maMap.setBuiltInZoomControls(true);        
		monControler = maMap.getController();
		monControler.setZoom(15);
		        				
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// Check if enabled and if not send user to the GSP settings. Better solution would be to display a dialog and suggesting to go to the settings
		if (!enabled) {
		  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  startActivity(intent);
		} 

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
		
		Criteria criteria = new Criteria();
	    String provider = lm.getBestProvider(criteria, false);
	    monLocation = lm.getLastKnownLocation(provider);
	    set(monLocation);
	    boolean filter = false;
	    Producer p = null;
	    try {
	    	Intent intent = getIntent();
	    	p = new Producer();
	    	p.raison_social = intent.getStringExtra("raison_social");
	    	p.latitude = intent.getDoubleExtra("latitude", 36.802997);
	    	p.longitude = intent.getDoubleExtra("longitude", 3.048706);
	    	p.sous_type = intent.getStringExtra("sous_type");
	    	p.address = intent.getStringExtra("address");
	    	p.mail = intent.getStringExtra("mail");
	    	p.telephone = intent.getStringExtra("telephone");
	    	p.ville = intent.getStringExtra("ville");
	    	filter = true;
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    if(filter && p!=null) {
	    	List<Producer> ps = new ArrayList<Producer>();
	    	ps.add(p);
	    	draw(ps);
	    }else {
		    draw(HomeActivity.producers);	    	
	    }
    }
    
    public void draw(List<Producer> producers) {
    	if(producers != null)
    	for(int i=0; i<producers.size() && i<30; i++) {
    		Producer p = producers.get(i);
    		GeoPoint point = new GeoPoint(microdegrees(p.latitude), microdegrees(p.longitude));
    		int drawable = Category.pin(p.sous_type);
    		if(drawable == -1)
    			drawable = R.drawable.marker;
        	ItemizedOverlayPerso pinOverlay = new ItemizedOverlayPerso(getResources().getDrawable(drawable));
    		pinOverlay.addPoint(point);
    		maMap.getOverlays().add(pinOverlay);
    		
    		// display small text (raison_social) on top of the pin
    		View popUp = getLayoutInflater().inflate(R.layout.map_popup, maMap, false);
    		TextView txt = (TextView) popUp.findViewById(R.id.txtMapPopup);
    		txt.setText(p.raison_social);
    		MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT, point, 10, -30,
                    MapView.LayoutParams.BOTTOM_CENTER);
    		maMap.addView(popUp, mapParams);
    	}
    }
    
    protected GeoPoint marker(Location location, int drawable) {
    	GeoPoint point = new GeoPoint(microdegrees(location.getLatitude()),microdegrees(location.getLongitude()));
    	ItemizedOverlayPerso pinOverlay = new ItemizedOverlayPerso(getResources().getDrawable(drawable));
		pinOverlay.addPoint(point);
		maMap.getOverlays().add(pinOverlay);
		return point;
    }
    
    protected void set(Location location) {
    	if (location != null) {
    		monLocation = location;
			Log.i(TAG, "Nouvelle position : " + location.getLatitude() + ", " + location.getLongitude());			
			monControler.animateTo(new GeoPoint(microdegrees(location.getLatitude()),microdegrees(location.getLongitude())));			
			monControler.setCenter(marker(location, R.drawable.marker));
		}
    }
    
	@Override
	public void onLocationChanged(Location location) {
		set(location);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        
    	menu.add(0,100,0,"Zoom In");
    	menu.add(0,101,0,"Zoom Out");
    	menu.add(0,102,0,"Satellite");
    	menu.add(0,103,0,"Trafic");
    	menu.add(0,104,0,"Street view");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case 100: monControler.setZoom(maMap.getZoomLevel() + 1)	;break;
    	case 101: monControler.setZoom(maMap.getZoomLevel() - 1)	;break;
    	case 102: maMap.setSatellite(!maMap.isSatellite())			;break;
    	case 103: maMap.setTraffic(!maMap.isTraffic())				;break;
    	case 104: maMap.setStreetView(!maMap.isStreetView())		;break;
    	}
    	return true;
    }
    
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(102).setIcon(maMap.isSatellite() ?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);
		menu.findItem(103).setIcon(maMap.isTraffic()   ?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);
		menu.findItem(104).setIcon(maMap.isStreetView()?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);
		return true;
	}
	
	private int microdegrees(double value){
		return (int)(value*1000000);
	}
			
	public class ItemizedOverlayPerso extends ItemizedOverlay<OverlayItem> {

	    private List<GeoPoint> points = new ArrayList<GeoPoint>();

	    public ItemizedOverlayPerso(Drawable defaultMarker) {
	        super(boundCenterBottom(defaultMarker));
	    }

	    @Override
	    protected OverlayItem createItem(int i) {
	        GeoPoint point = points.get(i);
	        return new OverlayItem(point,"Titre", "Description");
	    }

	    @Override
	    public int size() {
	        return points.size();
	    }

	    public void addPoint(GeoPoint point) {
	        this.points.add(point);
	        populate();
	    }
		
	    public void clearPoint() {
	        this.points.clear();
	        populate();
	    }
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
}
