package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.TraCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.TraCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesQuantity;

public class TraCatchProgressHandlerTest {

    @Test
    public void transhipmentDeclarationLoadedAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "DECLARATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("LOADED", "SPR", 30.0)
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),2);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getLoaded(),"COD",15.0);
        assertSpeciesQuantity(result.getLoaded(),"SPR",30.0);
    }

    @Test
    public void transhipmentDeclarationOnboardAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 5.0),
                makeFaCatch("ONBOARD", "SPR", 40.0)
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),2);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getOnBoard(),"COD",5.0);
        assertSpeciesQuantity(result.getOnBoard(),"SPR",40.0);
    }

    @Test
    public void transhipmentDeclarationUnloadedAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "DECLARATION",
                makeFaCatch("UNLOADED", "COD", 20.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),2);
        assertSpeciesQuantity(result.getUnLoaded(),"COD",20.0);
        assertSpeciesQuantity(result.getUnLoaded(),"SPR",50.0);
    }

    @Test
    public void transhipmentNotificationLoadedIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "NOTIFICATION",
                makeFaCatch("LOADED", "COD", 5.0),
                makeFaCatch("LOADED", "SPR", 40.0)
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),2);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getLoaded(),"COD",5.0);
        assertSpeciesQuantity(result.getLoaded(),"SPR",40.0);
    }

    @Test
    public void transhipmentNotificationUnloadedIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "NOTIFICATION",
                makeFaCatch("UNLOADED", "COD", 5.0),
                makeFaCatch("UNLOADED", "SPR", 40.0)
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),2);
        assertSpeciesQuantity(result.getUnLoaded(),"COD",5.0);
        assertSpeciesQuantity(result.getUnLoaded(),"SPR",40.0);
    }

    @Test
    public void transhipmentNotificationOnboardIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0)
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "NOTIFICATION", false);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),2);
        assertSpeciesNumber(result.getUnLoaded(),0);
        assertSpeciesQuantity(result.getOnBoard(),"COD",15.0);
        assertSpeciesQuantity(result.getOnBoard(),"SPR",30.0);
    }

    @Test
    public void transhipmentDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "TRANSHIPMENT",
                "DECLARATION"
        );

        TraCatchProgressHandler sut = new TraCatchProgressHandler();
        TraCatchProgressDTO result = (TraCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "TRANSHIPMENT", "DECLARATION", true);
        assertSpeciesNumber(result.getLoaded(),0);
        assertSpeciesNumber(result.getOnBoard(),0);
        assertSpeciesNumber(result.getUnLoaded(),0);
    }

}

