package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.ArrCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.ArrCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;


public class ArrCatchProgressHandlerTest {

    @Test
    public void arrivalDeclarationLoadedAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "DECLARATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("LOADED", "SPR", 30.0)
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "DECLARATION", false);
        assertSpeciesNumber(result.getLoaded(),2);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getLoaded(),"COD",15.0);
        assertSpeciesQuantity(result.getLoaded(),"SPR",30.0);
    }

    @Test
    public void arrivalDeclarationOnboardAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 5.0),
                makeFaCatch("ONBOARD", "SPR", 40.0)
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "DECLARATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),2);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getOnBoard(),"COD",5.0);
        assertSpeciesQuantity(result.getOnBoard(),"SPR",40.0);
    }

    @Test
    public void arrivalDeclarationUnloadedAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "DECLARATION",
                makeFaCatch("UNLOADED", "COD", 20.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "DECLARATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),2);
        assertSpeciesQuantity(result.getUnLoaded(),"COD",20.0);
        assertSpeciesQuantity(result.getUnLoaded(),"SPR",50.0);
    }

    @Test
    public void arrivalNotificationLoadedIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "NOTIFICATION",
                makeFaCatch("LOADED", "COD", 5.0),
                makeFaCatch("LOADED", "SPR", 40.0)
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),2);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getLoaded(),"COD",5.0);
        assertSpeciesQuantity(result.getLoaded(),"SPR",40.0);
    }

    @Test
    public void arrivalNotificationUnloadedIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "NOTIFICATION",
                makeFaCatch("UNLOADED", "COD", 5.0),
                makeFaCatch("UNLOADED", "SPR", 40.0)
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),2);
        assertSpeciesQuantity(result.getUnLoaded(),"COD",5.0);
        assertSpeciesQuantity(result.getUnLoaded(),"SPR",40.0);
    }

    @Test
    public void arrivalNotificationOnboardIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0)
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),2);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getOnBoard(),"COD",15.0);
        assertSpeciesQuantity(result.getOnBoard(),"SPR",30.0);
    }

    @Test
    public void arrivalDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "ARRIVAL",
                "DECLARATION"
        );

        ArrCatchProgressHandler sut = new ArrCatchProgressHandler();
        ArrCatchProgressDTO result = (ArrCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "ARRIVAL", "DECLARATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
    }
}
