package org.openimaj.tools.twitter.modes.filter;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.Option;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.twitter.TwitterStatus;

/**
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>, Sina Samangooei <ss@ecs.soton.ac.uk>
 *
 */
public class GeoFilter extends TwitterPreprocessingFilter {
	
	@Option(name="--bounding-box", aliases="-bb", required=false, usage="The bounding box to filter against.", metaVar="STRING")
	String boundBox = "51.28,-0.489,51.686,0.236";
	private Rectangle location = null;
	
	public GeoFilter(){
		
	}
	
	@Override
	public boolean filter(TwitterStatus twitterStatus) {
		if(twitterStatus.geo == null){
			return false;
		}
		location = getLocation();
		
		Map<String,Object> geomap = (Map<String, Object>) twitterStatus.geo;
//		"geo":{"type":"Point","coordinates":[51.55047862,-0.29938507]}
		List<Double> geolist = (List<Double>) geomap.get("coordinates");
		Point2d pos = new Point2dImpl(geolist.get(1).floatValue(),geolist.get(0).floatValue());
		return location.isInside(pos);
	}

	private Rectangle getLocation() {
		if(this.location == null){
			double minLon,minLat,maxLon,maxLat;
			String[] parts = boundBox.split(",");
			minLat = Double.parseDouble(parts[0]);
			minLon = Double.parseDouble(parts[1]);
			maxLat = Double.parseDouble(parts[2]);
			maxLon = Double.parseDouble(parts[3]);
			
			Point2d minp = new Point2dImpl((float)minLon,(float)minLat);
			Point2d maxp = new Point2dImpl((float)maxLon,(float)maxLat);
			this.location  = new Rectangle(minp,maxp);
		}
		return location;
	}
}