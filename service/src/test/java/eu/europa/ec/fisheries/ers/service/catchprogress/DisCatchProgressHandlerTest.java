package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.DisCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.DisCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;

public class DisCatchProgressHandlerTest {

    @Test
    public void discardDeclarationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "DECLARATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void discardNotificationAdds(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "NOTIFICATION",
                makeFaCatch("ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void discardDeclarationAddsTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void discardDeclarationIgnoresTakenOnBoard(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("ONBOARD", "SPR", 30.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0)
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),1);
        assertSpeciesQuantity(result.getTotal(),"SPR",160.0);
    }

    @Test
    public void discardDeclarationSubtracts(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "SPR", 40.0),
                makeFaCatch("LOADED", "SPR", 60.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 40.0),
                makeFaCatch("UNLOADED", "SPR", 50.0)
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",10.0);
    }

    @Test
    public void discardDeclarationDoesNotSubtract(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
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

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"FIS",80.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",130.0);
    }

    @Test
    public void discardDeclarationSubtractsAllSpeciesAreNegative(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "DECLARATION",
                makeFaCatch("TAKEN_ONBOARD", "COD", 15.0),
                makeFaCatch("DEMINIMIS", "SPR", 30.0),
                makeFaCatch("DISCARDED", "SPR", 10.0),
                makeFaCatch("UNLOADED", "SPR", 10.0)
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",15.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",-50.0);
    }

    @Test
    public void discardDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DISCARD",
                "DECLARATION"
        );

        DisCatchProgressHandler sut = new DisCatchProgressHandler();
        DisCatchProgressDTO result = (DisCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DISCARD", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
