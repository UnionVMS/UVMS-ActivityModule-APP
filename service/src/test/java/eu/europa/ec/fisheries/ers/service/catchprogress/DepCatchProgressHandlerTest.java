package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.DepCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.DepCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesQuantity;

public class DepCatchProgressHandlerTest {

    @Test
    public void departureDeclarationAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DEPARTURE",
                "DECLARATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "COD", 5.0)
        );

        DepCatchProgressHandler sut = new DepCatchProgressHandler();
        DepCatchProgressDTO result = (DepCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DEPARTURE", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",20.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",30.0);
    }

    @Test
    public void departureNotificationIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DEPARTURE",
                "NOTIFICATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "COD", 5.0)
        );

        DepCatchProgressHandler sut = new DepCatchProgressHandler();
        DepCatchProgressDTO result = (DepCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DEPARTURE", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",20.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",30.0);
    }

    @Test
    public void departureDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "DEPARTURE",
                "DECLARATION"
        );

        DepCatchProgressHandler sut = new DepCatchProgressHandler();
        DepCatchProgressDTO result = (DepCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "DEPARTURE", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
