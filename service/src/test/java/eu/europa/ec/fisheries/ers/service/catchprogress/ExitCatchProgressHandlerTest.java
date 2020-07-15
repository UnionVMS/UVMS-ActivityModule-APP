package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.ExitCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.ExitCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;

public class ExitCatchProgressHandlerTest {

    @Test
    public void exitDeclarationAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "AREA_EXIT",
                "DECLARATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "COD", 5.0)
        );

        ExitCatchProgressHandler sut = new ExitCatchProgressHandler();
        ExitCatchProgressDTO result = (ExitCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "AREA_EXIT", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",20.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",30.0);
    }

    @Test
    public void exitNotificationIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "AREA_EXIT",
                "NOTIFICATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "COD", 5.0)
        );

        ExitCatchProgressHandler sut = new ExitCatchProgressHandler();
        ExitCatchProgressDTO result = (ExitCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "AREA_EXIT", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",20.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",30.0);
    }

    @Test
    public void exitDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "AREA_EXIT",
                "DECLARATION"
        );

        ExitCatchProgressHandler sut = new ExitCatchProgressHandler();
        ExitCatchProgressDTO result = (ExitCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "AREA_EXIT", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
