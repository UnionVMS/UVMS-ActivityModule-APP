/*
 ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class LogbookModel {
	
	private List<VesselIdentifierLogBookModel>  vesselIdentifier;
	private List<PortLogBookModel> ports;
	private List<TripInfoLogBookModel> tripInfo;
	private List<FACatchModel> catches;
	private List<TranshipmentLandingModel> transhipmentLandings;
	private List<GearModel> gears;
	private List<MasterModel> master;
	private List<HeaderModel> header;

}
