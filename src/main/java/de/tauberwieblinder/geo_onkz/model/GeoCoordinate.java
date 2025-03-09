package de.tauberwieblinder.geo_onkz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoCoordinate {
    private double latitude;
    private double longitude;
}