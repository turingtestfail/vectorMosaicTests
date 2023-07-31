package org.geotools.vectormosaic;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.visitor.MaxVisitor;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Instant start2 = Instant.now();
        SimpleFeatureCollection fcAll = featureSource.getFeatures();
        try (SimpleFeatureIterator iterator = fcAll.features(); ) {
            int count = 0;
            while (iterator.hasNext()) {
                iterator.next();
                count++;
            }
            assertEquals(32178, count);
        }
        assertEquals(55, tracker.size());
        Instant finish2 = Instant.now();
        long timeElapsed2 = Duration.between(start2, finish2).toMillis();
        System.out.println("Mosaic Query Time elapsed: " + timeElapsed + "ms");
        System.out.println("Mosaic All Features Time elapsed: " + timeElapsed2 + "ms");
        assertTrue(timeElapsed < timeElapsed2);
    }

    @Test
    public void testGetMax() throws Exception {
        Instant start = Instant.now();
        SimpleFeatureSource featureSource = MOSAIC_STORE.getFeatureSource(MOSAIC_TYPE_NAME);
        Set<String> tracker = new HashSet<>();
        ((VectorMosaicFeatureSource) featureSource).granuleTracker = tracker;
        PropertyName p = FF.property("rank");
        Query q = new Query();
        q.setPropertyNames(new String[]{"rank"});
        Filter f = FF.lessOrEqual(p, FF.literal(100));
        q.setFilter(f);

        MaxVisitor v = new MaxVisitor(p);
        ((VectorMosaicFeatureSource) featureSource).accepts(q, v,null);
        int max = (int) v.getMax();
    }
}
