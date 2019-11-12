package eu.europa.ec.fisheries.ers.activity.service.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsTest {

    @Test
    public void addTwoNulls() {
        // Given

        // When
        Double result = Utils.addDoubles(null, null);

        // Then
        assertNull(result);
    }

    @Test
    public void actualMeasureToAddIsNull() {
        // Given

        // When
        Double result = Utils.addDoubles(null, 15.67d);

        // Then
        assertEquals(15.67d, result, 0.1d);
    }

    @Test
    public void measureSubTotalToAddToIsNull() {
        // Given

        // When
        Double result = Utils.addDoubles(3.14d, null);

        // Then
        assertEquals(3.14d, result, 0.1d);
    }

    @Test
    public void addTwoDoubleTogether() {
        // Given

        // When
        Double result = Utils.addDoubles(1.23d, 3.21d);

        // Then
        assertEquals(4.44d, result, 0.1d);
    }
}
