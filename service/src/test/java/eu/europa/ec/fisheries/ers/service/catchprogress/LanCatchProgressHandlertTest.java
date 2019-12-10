package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.LanCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.LanCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesQuantity;

public class LanCatchProgressHandlertTest {

    @Test
    public void landingDeclarationLoadedAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "DECLARATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("LOADED", "SPR", 30.0)
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),2);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getLoaded(),"COD",15.0);
        assertSpeciesQuantity(result.getLoaded(),"SPR",30.0);
    }

    @Test
    public void landingDeclarationOnboardAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 5.0),
                makeFaCatch("ONBOARD", "SPR", 40.0)
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),2);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getOnBoard(),"COD",5.0);
        assertSpeciesQuantity(result.getOnBoard(),"SPR",40.0);
    }

    @Test
    public void landingDeclarationUnloadedAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "DECLARATION",
                makeFaCatch("UNLOADED", "COD", 20.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),2);
        assertSpeciesQuantity(result.getUnLoaded(),"COD",20.0);
        assertSpeciesQuantity(result.getUnLoaded(),"SPR",50.0);
    }

    @Test
    public void landingNotificationLoadedIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "NOTIFICATION",
                makeFaCatch("LOADED", "COD", 5.0),
                makeFaCatch("LOADED", "SPR", 40.0)
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),2);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getLoaded(),"COD",5.0);
        assertSpeciesQuantity(result.getLoaded(),"SPR",40.0);
    }

    @Test
    public void landingNotificationUnloadedIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "NOTIFICATION",
                makeFaCatch("UNLOADED", "COD", 5.0),
                makeFaCatch("UNLOADED", "SPR", 40.0)
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),2);
        assertSpeciesQuantity(result.getUnLoaded(),"COD",5.0);
        assertSpeciesQuantity(result.getUnLoaded(),"SPR",40.0);
    }

    @Test
    public void landingNotificationOnboardIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0)
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),2);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getOnBoard(),"COD",15.0);
        assertSpeciesQuantity(result.getOnBoard(),"SPR",30.0);
    }

    @Test
    public void landingDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "LANDING",
                "DECLARATION"
        );

        LanCatchProgressHandler sut = new LanCatchProgressHandler();
        LanCatchProgressDTO result = (LanCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "LANDING", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
    }

}
