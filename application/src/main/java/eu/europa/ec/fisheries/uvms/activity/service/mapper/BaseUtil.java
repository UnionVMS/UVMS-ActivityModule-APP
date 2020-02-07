package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import java.util.List;

public abstract class BaseUtil {


    public static String getLanguageIdFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getLanguageID();
    }

    public static String getTextFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getValue();
    }


    public static boolean getCorrection(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || StringUtils.isEmpty(entity.getFaReportDocument().getFluxReportDocument_Id())) {
            return false;
        }

        return entity.getFaReportDocument().getFluxReportDocument_ReferencedFaReportDocumentId() != null &&
                entity.getFaReportDocument().getFluxReportDocument_ReferencedFaReportDocumentId().length() != 0;
    }

}
