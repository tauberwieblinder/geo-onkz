package de.tauberwieblinder.geo_onkz.controller;

import de.tauberwieblinder.geo_onkz.model.AreaCodeResponse;
import de.tauberwieblinder.geo_onkz.model.GeoCoordinate;
import de.tauberwieblinder.geo_onkz.service.ShapefileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/areacode")
@RequiredArgsConstructor
public class AreaCodeController {

    private final ShapefileService shapefileService;

    @GetMapping
    public ResponseEntity<AreaCodeResponse> getAreaCode(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude) {

        log.info("Received request for coordinates: lat={}, lon={}", latitude, longitude);
        AreaCodeResponse response = shapefileService.findAreaCode(new GeoCoordinate(latitude, longitude));

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}