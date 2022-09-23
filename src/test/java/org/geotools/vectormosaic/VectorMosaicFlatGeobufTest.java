package org.geotools.vectormosaic;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.opengis.filter.Filter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class VectorMosaicFlatGeobufTest extends VectorMosaicTest{
    @Test
    public void testGetCount() throws Exception {
        Instant start = Instant.now();
        SimpleFeatureSource featureSource = MOSAIC_STORE.getFeatureSource(MOSAIC_TYPE_NAME);
        Set<String> tracker = new HashSet<>();
        ((VectorMosaicFeatureSource) featureSource).granuleTracker = tracker;
        Query q = new Query();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );

        WKTReader reader = new WKTReader( geometryFactory );
        Polygon polygon = (Polygon) reader.read("POLYGON((-82 40, -76 40, -76 36, -82 36, -82 40))");
        Filter intersects =
                FF.intersects(
                        FF.property("geom"),
                        FF.literal(polygon));
        q.setFilter(intersects);
        SimpleFeatureCollection fc = featureSource.getFeatures(q);
        try (SimpleFeatureIterator iterator = fc.features(); ) {
            int count = 0;
            while (iterator.hasNext()) {
                iterator.next();
                count++;
            }
            assertEquals(1826, count);
        }
        assertEquals(8, tracker.size());
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
    }
}
