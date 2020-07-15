package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.JfopCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.JfopCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;

public class JfopCatchProgressHandlerTest {

    @Test
    public void joinedFishingOperationDeclarationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void joinedFishingOperationNotificationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void joinedFishingOperationDeclarationAddsTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void joinedFishingOperationDeclarationIgnoresTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),1);
        assertSpeciesQuantity(result.getTotal(),"SPR",160.0);
    }

    @Test
    public void joinedFishingOperationDeclarationSubtracts(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 40.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",10.0);
    }

    @Test
    public void joinedFishingOperationDeclarationDoesNotSubtract(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
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

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"FIS",80.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void joinedFishingOperationDeclarationSubtractsAllSpeciesAreNegative(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 10.0),
                makeFaCatch("UNLOADED", "SPR", 10.0)
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",-50.0);
    }

    @Test
    public void joinedFishingOperationDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "JOINED_FISHING_OPERATION",
                "DECLARATION"
        );

        JfopCatchProgressHandler sut = new JfopCatchProgressHandler();
        JfopCatchProgressDTO result = (JfopCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "JOINED_FISHING_OPERATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
