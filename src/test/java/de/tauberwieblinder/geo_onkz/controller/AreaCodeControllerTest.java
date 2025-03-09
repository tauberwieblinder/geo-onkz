package de.tauberwieblinder.geo_onkz.controller;

import de.tauberwieblinder.geo_onkz.model.AreaCodeResponse;
import de.tauberwieblinder.geo_onkz.service.ShapefileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AreaCodeControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ShapefileService shapefileService() {
            return Mockito.mock(ShapefileService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShapefileService shapefileService;

    @Test
    void shouldReturnAreaCodeWhenCoordinatesAreValid() throws Exception {
        // Given
        double latitude = 52.516667;
        double longitude = 13.388889;
        String areaCode = "30";
        AreaCodeResponse response = new AreaCodeResponse(areaCode);

        when(shapefileService.findAreaCode(any())).thenReturn(response);

        // When/Then
        mockMvc.perform(get("/api/areacode")
                        .param("lat", String.valueOf(latitude))
                        .param("lon", String.valueOf(longitude)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areaCode").value(areaCode));
    }

    @Test
    void shouldReturnNotFoundWhenNoAreaCodeExists() throws Exception {
        // Given
        double latitude = 0.0;
        double longitude = 0.0;

        when(shapefileService.findAreaCode(any())).thenReturn(null);

        // When/Then
        mockMvc.perform(get("/api/areacode")
                        .param("lat", String.valueOf(latitude))
                        .param("lon", String.valueOf(longitude)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenParametersAreMissing() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/areacode"))
                .andExpect(status().isBadRequest());
    }
}