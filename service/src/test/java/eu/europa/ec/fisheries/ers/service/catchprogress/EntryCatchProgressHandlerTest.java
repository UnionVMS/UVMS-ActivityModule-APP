package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.EntryCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.EntryCatchProgressHandler;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.*;
import static eu.europa.ec.fisheries.ers.service.catchprogress.CatchProgressHandlerUtil.assertSpeciesNumber;

public class EntryCatchProgressHandlerTest {

    @Test
    public void entryDeclarationAffects(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "AREA_ENTRY",
                "DECLARATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "COD", 5.0)
        );

        EntryCatchProgressHandler sut = new EntryCatchProgressHandler();
        EntryCatchProgressDTO result = (EntryCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "AREA_ENTRY", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",20.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",30.0);
    }

    @Test
    public void entryNotificationIgnores(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "AREA_ENTRY",
                "NOTIFICATION",
                makeFaCatch("LOADED", "COD", 15.0),
                makeFaCatch("KEPT_IN_NET", "SPR", 30.0),
                makeFaCatch("BY_CATCH", "COD", 5.0)
        );

        EntryCatchProgressHandler sut = new EntryCatchProgressHandler();
        EntryCatchProgressDTO result = (EntryCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "AREA_ENTRY", "NOTIFICATION", false);
        assertSpeciesNumber(result.getTotal(),2);
        assertSpeciesQuantity(result.getTotal(),"COD",20.0);
        assertSpeciesQuantity(result.getTotal(),"SPR",30.0);
    }

    @Test
    public void entryDeclarationEmpty(){
        FishingActivityEntity fishingActivity = makeFishingActivityEntity(
                "AREA_ENTRY",
                "DECLARATION"
        );

        EntryCatchProgressHandler sut = new EntryCatchProgressHandler();
        EntryCatchProgressDTO result = (EntryCatchProgressDTO) sut.prepareCatchProgressDTO(fishingActivity);

        assertCatchProgressDTO(result, "AREA_ENTRY", "DECLARATION", true);
        assertSpeciesNumber(result.getTotal(),0);
    }
}
