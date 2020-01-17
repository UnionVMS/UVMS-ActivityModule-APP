package eu.europa.ec.fisheries.uvms.activity.service.dto;

import org.junit.Test;

import static org.junit.Assert.*;

public class DelimitedPeriodDTOTest {
    @Test
    public void name() {
        // Given

        // When
        DelimitedPeriodDTO delimitedPeriodDTO = DelimitedPeriodDTO
                .builder()
                .duration(24d)
                .build();

        // Then
        assertEquals(24d, delimitedPeriodDTO.getDuration(), 0.0d);
    }
}
