package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class AapProductEntityTest {

    @Test
    public void prePersist_unitQuantity() {
        // Given
        AapProductEntity aapProductEntity = new AapProductEntity();
        aapProductEntity.setUnitQuantity(45d);
        aapProductEntity.setUnitQuantityCode("TNE");

        // When
        aapProductEntity.prePersist();

        // Then
        Double calculatedUnitQuantity = aapProductEntity.getCalculatedUnitQuantity();
        assertEquals(45000d, calculatedUnitQuantity, 0.0);
    }

    @Test
    public void prePersist_packagingUnit() {
        // Given
        AapProductEntity aapProductEntity = new AapProductEntity();
        aapProductEntity.setPackagingUnitCount(45d);
        aapProductEntity.setPackagingUnitCountCode("MTR");

        // When
        aapProductEntity.prePersist();

        // Then
        Double calculatedPackagingUnitCount = aapProductEntity.getCalculatedPackagingUnitCount();
        assertEquals(45000d, calculatedPackagingUnitCount, 0.0);
    }
}
