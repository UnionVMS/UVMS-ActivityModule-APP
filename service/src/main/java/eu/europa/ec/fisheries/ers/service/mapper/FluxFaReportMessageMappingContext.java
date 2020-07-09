/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.schema.movement.v1.MovementMetaDataAreaType;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;

@Getter
public class FluxFaReportMessageMappingContext {
	private Map<FishingActivity, FishingActivityEntity> activitiesMap = new HashMap<>();
	private Map<FishingActivityEntity, FishingActivity> entitiesMap = new HashMap<>();
	private Map<FishingActivityEntity, List<MovementMetaDataAreaType>> areasMap = new HashMap<>();
	private Map<Pair<String, Date>, String> assetHistoryGuidsMap = new HashMap<>();

	public void put(FishingActivity activity, FishingActivityEntity entity) {
		activitiesMap.put(activity, entity);
		entitiesMap.put(entity, activity);
	}

	public void put(FishingActivityEntity activity, List<MovementMetaDataAreaType> areas) {
		areasMap.put(activity, areas);
	}

	public void put(Pair<String, Date> assetGuidWithDate, String assetHistoryGuid) {
		assetHistoryGuidsMap.put(assetGuidWithDate, assetHistoryGuid);
	}

	public FishingActivityEntity getFishingActivityEntity(FishingActivity activity) {
		return activitiesMap.get(activity);
	}

	public FishingActivity getFishingActivity(FishingActivityEntity entity) {
		return entitiesMap.get(entity);
	}

	public List<MovementMetaDataAreaType> getAreasForEntity(FishingActivityEntity entity) {
		return areasMap.get(entity);
	}

	public String getAssetHistoryGuid(Pair<String, Date> assetGuidWithDate) {
		return assetHistoryGuidsMap.get(assetGuidWithDate);
	}
}
