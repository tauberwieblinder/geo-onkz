package de.tauberwieblinder.geo_onkz.controller;

import de.tauberwieblinder.geo_onkz.model.AreaCodeResponse;
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

    @GetMapping
    public ResponseEntity<AreaCodeResponse> getAreaCode(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude) {

        log.info("Received request for coordinates: lat={}, lon={}", latitude, longitude);
        AreaCodeResponse response = new AreaCodeResponse("5673");   // mocked for now

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}