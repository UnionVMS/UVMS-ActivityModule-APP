package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.RlcCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.RlcCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;

public class RlcCatchProgressHandlerTest {

    @Test
    public void relocationDeclarationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void relocationNotificationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void relocationDeclarationAddsTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void relocationDeclarationIgnoresTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),1);
        assertSpeciesQuantity(result.getTotal(),"SPR",160.0);
    }

    @Test
    public void relocationDeclarationSubtracts(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 40.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",10.0);
    }

    @Test
    public void relocationDeclarationDoesNotSubtract(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
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

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"FIS",80.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void relocationDeclarationSubtractsAllSpeciesAreNegative(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 10.0),
                makeFaCatch("UNLOADED", "SPR", 10.0)
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",-50.0);
    }

    @Test
    public void relocationDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "RELOCATION",
                "DECLARATION"
        );

        RlcCatchProgressHandler sut = new RlcCatchProgressHandler();
        RlcCatchProgressDTO result = (RlcCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "RELOCATION", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
