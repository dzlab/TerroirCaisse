package com.terroir.caisse.helper;

import java.util.Comparator;
import android.location.Location;
import com.terroir.caisse.data.Producer;

public class DistanceComparator implements Comparator<Producer> {
	
	public Location currentLocation = new Location("current location");

	@Override
    public int compare(Producer o1, Producer o2) {
		o1.distance = computeDistance(currentLocation , o1);
		o2.distance = computeDistance(currentLocation , o2);
    	
        return (o1.distance>o2.distance ? -1 : (o1.distance==o2.distance ? 0 : 1));
    }
	public float computeDistance(Location location, Producer producer) {
		float[] res = new float[1];
		Location.distanceBetween(location.getLatitude(), location.getLongitude(), producer.latitude, producer.longitude, res);
		return res[0];
	}

}
