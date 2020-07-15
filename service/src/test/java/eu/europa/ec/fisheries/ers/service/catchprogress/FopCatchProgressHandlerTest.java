package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.FopCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.FopCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesQuantity;


public class FopCatchProgressHandlerTest {

    @Test
    public void fishingOperationDeclarationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void fishingOperationNotificationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void fishingOperationDeclarationAddsTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void fishingOperationDeclarationIgnoresTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),1);
        assertSpeciesQuantity(result.getTotal(),"SPR",160.0);
    }

    @Test
    public void fishingOperationDeclarationSubtracts(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 40.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",10.0);
    }

    @Test
    public void fishingOperationDeclarationDoesNotSubtract(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "FIS", 80.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 40.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"FIS",80.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void fishingOperationDeclarationSubtractsAllSpeciesAreNegative(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 10.0),
                makeFaCatch("UNLOADED", "SPR", 10.0)
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",-50.0);
    }

    @Test
    public void fishingOperationDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "FISHING_OPERATION",
                "DECLARATION"
        );

        FopCatchProgressHandler sut = new FopCatchProgressHandler();
        FopCatchProgressDTO result = (FopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
