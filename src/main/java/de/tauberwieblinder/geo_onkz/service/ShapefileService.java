package de.tauberwieblinder.geo_onkz.service;

import de.tauberwieblinder.geo_onkz.model.AreaCodeResponse;
import de.tauberwieblinder.geo_onkz.model.GeoCoordinate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShapefileService {
    private SimpleFeatureSource featureSource;
    private MathTransform transform;

    @Value("${shapefile.path}")
    private String shapefilePath;

    private final ResourceLoader resourceLoader;
    private GeometryFactory geometryFactory;

    @PostConstruct
    public void init() {
        try {
            geometryFactory = JTSFactoryFinder.getGeometryFactory();

            Resource resource = resourceLoader.getResource(shapefilePath);
            File file = resource.getFile();

            if (file.exists() && file.isFile()) {
                log.info("Shapefile found at: {}", file.getAbsolutePath());

                Map<String, Object> map = new HashMap<>();
                map.put("url", file.toURI().toURL());

                DataStore dataStore = DataStoreFinder.getDataStore(map);
                String typeName = dataStore.getTypeNames()[0];
                featureSource = dataStore.getFeatureSource(typeName);

                // get source coordinate reference system (CRS) from the shapefile
                SimpleFeatureType schema = featureSource.getSchema();
                CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();

                // target CRS is WGS84 (standard GPS coordinates)
                CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;

                // create transform between coordinate systems
                transform = CRS.findMathTransform(targetCRS, sourceCRS, true);

                log.info("Shapefile loaded successfully: {}", typeName);
            } else {
                log.error("Shapefile not found at: {}", shapefilePath);
            }
        } catch (Exception e) {
            log.error("Error loading shapefile: {}", e.getMessage(), e);
        }
    }

    public AreaCodeResponse findAreaCode(GeoCoordinate coordinate) {
        if (featureSource == null) {
            log.error("Feature source not initialized");
            return null;
        }

        try {
            // create a point from the WGS84 coordinates (standard GPS)
            Point point = geometryFactory.createPoint(
                    new Coordinate(coordinate.getLongitude(), coordinate.getLatitude()));

            // transform point to the shapefile's coordinate system
            Point transformedPoint = (Point) JTS.transform(point, transform);

            SimpleFeatureCollection features = featureSource.getFeatures();
            try (SimpleFeatureIterator iterator = features.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    org.locationtech.jts.geom.Geometry geometry =
                            (org.locationtech.jts.geom.Geometry) feature.getDefaultGeometry();

                    if (geometry.contains(transformedPoint)) {
                        // Determine which attribute contains the area code (ONB_NUMMER based on dbf scan)
                        Object onbNummerAttr = feature.getAttribute("ONB_NUMMER");
                        String areaCode = onbNummerAttr != null ? onbNummerAttr.toString() : "Unknown";

                        return new AreaCodeResponse(areaCode);
                    }
                }
            }

            log.info("No area code found for coordinates: {}", coordinate);
        } catch (Exception e) {
            log.error("Error querying shapefile: {}", e.getMessage(), e);
        }

        return null;
    }
}