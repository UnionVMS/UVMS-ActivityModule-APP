package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class FishingTripIdWithGeometryMapperTest {
    private static FishingTripId tripId = new FishingTripId("MLT-TRP-20160630000001","EU_TRIP_ID");
    private  static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final double DELTA = 1e-15;

    @Test
    public void tripDurationWithDepartureWithoutArrival() throws ParseException {
        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(df.parse("2019-01-09 12:32:00"));
        fishingActivityEntity1.setTypeCode(FishingActivityTypeEnum.DEPARTURE.toString());
        FishingActivityEntity fishingActivityEntity2 = new FishingActivityEntity();
        fishingActivityEntity2.setCalculatedStartTime(df.parse("2019-01-11 12:32:00"));
        fishingActivityEntity2.setTypeCode(FishingActivityTypeEnum.DEPARTURE.toString());
        FishingActivityEntity fishingActivityEntity3 = new FishingActivityEntity();
        fishingActivityEntity3.setCalculatedStartTime(df.parse("2019-01-10 12:32:00"));
        fishingActivityEntity3.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity4 = new FishingActivityEntity();
        fishingActivityEntity4.setCalculatedStartTime(df.parse("2019-02-11 12:32:00"));
        fishingActivityEntity4.setTypeCode(FishingActivityTypeEnum.AREA_ENTRY.toString());
        FishingActivityEntity fishingActivityEntity5 = new FishingActivityEntity();
        fishingActivityEntity5.setCalculatedStartTime(df.parse("2019-02-08 12:32:00"));
        fishingActivityEntity5.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity6 = new FishingActivityEntity();
        fishingActivityEntity6.setCalculatedStartTime(df.parse("2019-02-12 12:32:00"));
        fishingActivityEntity6.setTypeCode(FishingActivityTypeEnum.AREA_ENTRY.toString());
        fishingActivityEntities.add(fishingActivityEntity1);
        fishingActivityEntities.add(fishingActivityEntity2);
        fishingActivityEntities.add(fishingActivityEntity3);
        fishingActivityEntities.add(fishingActivityEntity4);
        fishingActivityEntities.add(fishingActivityEntity5);
        fishingActivityEntities.add(fishingActivityEntity6);

        FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper = new FishingTripIdWithGeometryMapper();
        FishingTripIdWithGeometry fishingTripIdWithGeometry =  fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(tripId, fishingActivityEntities);
        double tripDuration = fishingTripIdWithGeometry.getTripDuration();
        double expectedTimeDiff = (double) (df.parse("2019-02-12 12:32:00").getTime() - df.parse("2019-01-09 12:32:00").getTime());
        assertEquals(expectedTimeDiff,tripDuration,DELTA);
    }

    @Test
    public void tripDurationWithDepartureWithArrival() throws ParseException {

        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(df.parse("2019-01-09 12:32:00"));
        fishingActivityEntity1.setTypeCode(FishingActivityTypeEnum.DEPARTURE.toString());
        FishingActivityEntity fishingActivityEntity2 = new FishingActivityEntity();
        fishingActivityEntity2.setCalculatedStartTime(df.parse("2019-01-11 12:32:00"));
        fishingActivityEntity2.setTypeCode(FishingActivityTypeEnum.DEPARTURE.toString());
        FishingActivityEntity fishingActivityEntity3 = new FishingActivityEntity();
        fishingActivityEntity3.setCalculatedStartTime(df.parse("2019-01-10 12:32:00"));
        fishingActivityEntity3.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity4 = new FishingActivityEntity();
        fishingActivityEntity4.setCalculatedStartTime(df.parse("2019-02-11 14:32:00"));
        fishingActivityEntity4.setTypeCode(FishingActivityTypeEnum.ARRIVAL.toString());
        FishingActivityEntity fishingActivityEntity5 = new FishingActivityEntity();
        fishingActivityEntity5.setCalculatedStartTime(df.parse("2019-02-08 12:32:00"));
        fishingActivityEntity5.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity6 = new FishingActivityEntity();
        fishingActivityEntity6.setCalculatedStartTime(df.parse("2019-02-12 15:32:00"));
        fishingActivityEntity6.setTypeCode(FishingActivityTypeEnum.ARRIVAL.toString());
        fishingActivityEntities.add(fishingActivityEntity1);
        fishingActivityEntities.add(fishingActivityEntity2);
        fishingActivityEntities.add(fishingActivityEntity3);
        fishingActivityEntities.add(fishingActivityEntity4);
        fishingActivityEntities.add(fishingActivityEntity5);
        fishingActivityEntities.add(fishingActivityEntity6);

        FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper = new FishingTripIdWithGeometryMapper();
        FishingTripIdWithGeometry fishingTripIdWithGeometry =  fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(tripId, fishingActivityEntities);
        double tripDuration = fishingTripIdWithGeometry.getTripDuration();
        double expectedTimeDiff = (double) (df.parse("2019-02-12 15:32:00").getTime() - df.parse("2019-01-09 12:32:00").getTime());
        assertEquals(expectedTimeDiff,tripDuration,DELTA);
    }

    @Test
    public void tripDurationWithoutDepartureWithArrival() throws ParseException {

        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(df.parse("2019-01-05 12:32:00"));
        fishingActivityEntity1.setTypeCode(FishingActivityTypeEnum.LANDING.toString());
        FishingActivityEntity fishingActivityEntity2 = new FishingActivityEntity();
        fishingActivityEntity2.setCalculatedStartTime(df.parse("2019-01-11 12:32:00"));
        fishingActivityEntity2.setTypeCode(FishingActivityTypeEnum.RELOCATION.toString());
        FishingActivityEntity fishingActivityEntity3 = new FishingActivityEntity();
        fishingActivityEntity3.setCalculatedStartTime(df.parse("2019-01-10 12:32:00"));
        fishingActivityEntity3.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity4 = new FishingActivityEntity();
        fishingActivityEntity4.setCalculatedStartTime(df.parse("2019-02-11 14:32:00"));
        fishingActivityEntity4.setTypeCode(FishingActivityTypeEnum.ARRIVAL.toString());
        FishingActivityEntity fishingActivityEntity5 = new FishingActivityEntity();
        fishingActivityEntity5.setCalculatedStartTime(df.parse("2019-02-08 12:32:00"));
        fishingActivityEntity5.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity6 = new FishingActivityEntity();
        fishingActivityEntity6.setCalculatedStartTime(df.parse("2019-02-12 15:32:00"));
        fishingActivityEntity6.setTypeCode(FishingActivityTypeEnum.ARRIVAL.toString());
        fishingActivityEntities.add(fishingActivityEntity1);
        fishingActivityEntities.add(fishingActivityEntity2);
        fishingActivityEntities.add(fishingActivityEntity3);
        fishingActivityEntities.add(fishingActivityEntity4);
        fishingActivityEntities.add(fishingActivityEntity5);
        fishingActivityEntities.add(fishingActivityEntity6);

        FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper = new FishingTripIdWithGeometryMapper();
        FishingTripIdWithGeometry fishingTripIdWithGeometry =  fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(tripId, fishingActivityEntities);
        double tripDuration = fishingTripIdWithGeometry.getTripDuration();
        double expectedTimeDiff = (double) (df.parse("2019-02-12 15:32:00").getTime() - df.parse("2019-01-05 12:32:00").getTime());
        assertEquals(expectedTimeDiff,tripDuration,DELTA);
    }

    @Test
    public void tripDurationWithoutDepartureWithoutArrival() throws ParseException {

        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(df.parse("2019-01-05 12:32:00"));
        fishingActivityEntity1.setTypeCode(FishingActivityTypeEnum.LANDING.toString());
        FishingActivityEntity fishingActivityEntity2 = new FishingActivityEntity();
        fishingActivityEntity2.setCalculatedStartTime(df.parse("2019-01-11 12:32:00"));
        fishingActivityEntity2.setTypeCode(FishingActivityTypeEnum.RELOCATION.toString());
        FishingActivityEntity fishingActivityEntity3 = new FishingActivityEntity();
        fishingActivityEntity3.setCalculatedStartTime(df.parse("2019-01-10 12:32:00"));
        fishingActivityEntity3.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity4 = new FishingActivityEntity();
        fishingActivityEntity4.setCalculatedStartTime(df.parse("2019-02-11 14:32:00"));
        fishingActivityEntity4.setTypeCode(FishingActivityTypeEnum.GEAR_RETRIEVAL.toString());
        FishingActivityEntity fishingActivityEntity5 = new FishingActivityEntity();
        fishingActivityEntity5.setCalculatedStartTime(df.parse("2019-02-08 12:32:00"));
        fishingActivityEntity5.setTypeCode(FishingActivityTypeEnum.DISCARD.toString());
        FishingActivityEntity fishingActivityEntity6 = new FishingActivityEntity();
        fishingActivityEntity6.setCalculatedStartTime(df.parse("2019-02-12 15:32:00"));
        fishingActivityEntity6.setTypeCode(FishingActivityTypeEnum.RELOCATION.toString());
        fishingActivityEntities.add(fishingActivityEntity1);
        fishingActivityEntities.add(fishingActivityEntity2);
        fishingActivityEntities.add(fishingActivityEntity3);
        fishingActivityEntities.add(fishingActivityEntity4);
        fishingActivityEntities.add(fishingActivityEntity5);
        fishingActivityEntities.add(fishingActivityEntity6);

        FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper = new FishingTripIdWithGeometryMapper();
        FishingTripIdWithGeometry fishingTripIdWithGeometry =  fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(tripId, fishingActivityEntities);
        double tripDuration = fishingTripIdWithGeometry.getTripDuration();
        double expectedTimeDiff = (double) (df.parse("2019-02-12 15:32:00").getTime() - df.parse("2019-01-05 12:32:00").getTime());
        assertEquals(expectedTimeDiff,tripDuration,DELTA);
    }

    @Test
    public void tripDurationWithOneActivity() throws ParseException {

        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(df.parse("2019-01-05 12:32:00"));
        fishingActivityEntity1.setTypeCode(FishingActivityTypeEnum.LANDING.toString());
        fishingActivityEntities.add(fishingActivityEntity1);

        FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper = new FishingTripIdWithGeometryMapper();
        FishingTripIdWithGeometry fishingTripIdWithGeometry =  fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(tripId, fishingActivityEntities);
        double tripDuration = fishingTripIdWithGeometry.getTripDuration();
        double expectedTimeDiff = 0d;
        assertEquals(expectedTimeDiff,tripDuration,DELTA);
    }

    @Test
    public void tripDurationWithNoActivity()  {

        FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper = new FishingTripIdWithGeometryMapper();
        FishingTripIdWithGeometry fishingTripIdWithGeometry =  fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(tripId, null);
        double tripDuration = fishingTripIdWithGeometry.getTripDuration();
        double expectedTimeDiff = 0d;
        assertEquals(expectedTimeDiff,tripDuration,DELTA);
    }

}