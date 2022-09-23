package org.geotools.vectormosaic;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultRepository;
import org.geotools.data.flatgeobuf.FlatGeobufDataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.vectormosaic.VectorMosaicStoreFactory;
import org.junit.Before;
import org.opengis.filter.FilterFactory2;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VectorMosaicTest {
    public static DefaultRepository REPOSITORY;
    public static VectorMosaicStoreFactory VECTOR_MOSAIC_STORE_FACTORY;
    protected static final String MOSAIC_TYPE_NAME = "tileindex_tiger_mosaic";
    public static DataStore MOSAIC_STORE;
    protected static FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();
    static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;
        try {
            REPOSITORY = new DefaultRepository();

            URL url = new URL("file:///home/PIXIA.LOCAL/millerj/data/tiger/tileindex_tiger.fgb");

            FlatGeobufDataStore ds =
                     new FlatGeobufDataStore(url);
            REPOSITORY.register("delegate", ds);
            VECTOR_MOSAIC_STORE_FACTORY = new VectorMosaicStoreFactory();
            Map<String, Object> params = new HashMap<>();
            params.put(VectorMosaicStoreFactory.DELEGATE_STORE_NAME.getName(), "delegate");
            params.put(VectorMosaicStoreFactory.NAMESPACE.getName(), "topp");
            params.put(VectorMosaicStoreFactory.REPOSITORY_PARAM.getName(), REPOSITORY);
            MOSAIC_STORE = VECTOR_MOSAIC_STORE_FACTORY.createDataStore(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initialized = true;
    }

    @Before
    public void setup() {
        initialize();
    }
}
