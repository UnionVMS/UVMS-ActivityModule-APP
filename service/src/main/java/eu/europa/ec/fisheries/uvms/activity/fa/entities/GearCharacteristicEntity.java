/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.UnitCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "activity_gear_characteristic")
@ToString
@Builder(toBuilder = true, builderMethodName = "") // Prevents Lombok from creating builder that removes field initializers
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GearCharacteristicEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "gear_char_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "type_code", nullable = false)
    @NotNull
	private String typeCode = StringUtils.EMPTY;

	@Column(name = "type_code_list_id", nullable = false)
    @NotNull
	private String typeCodeListId = StringUtils.EMPTY;

	private String description;

	@Column(name = "desc_language_id")
	private String descLanguageId;

	@Column(name = "value_measure", precision = 17, scale = 17)
	private Double valueMeasure;

	@Column(name = "value_measure_unit_code")
	private String valueMeasureUnitCode;

	@Column(name = "calculated_value_measure")
	private Double calculatedValueMeasure;

	@Column(name = "value_date_time")
	private Instant valueDateTime;

	@Column(name = "value_indicator")
	private String valueIndicator;

	@Column(name = "value_code")
	private String valueCode;

	@Column(name = "value_text")
	private String valueText;

    @Column(name = "value_quantity", precision = 17, scale = 17)
	private Double valueQuantity;

	@Column(name = "value_quantity_code")
	private String valueQuantityCode;

	@Column(name = "calculated_value_quantity")
	private Double calculatedValueQuantity;

	@ManyToOne
	@JoinColumn(name = "fishing_gear_id")
	private FishingGearEntity fishingGear;

	@PrePersist
	public void prePersist(){
		if (valueQuantityCode != null || valueQuantity != null){
			UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(valueQuantityCode);
			if (unitCodeEnum != null) {
				BigDecimal quantity = new BigDecimal(valueQuantity);
				BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
				calculatedValueQuantity =  result.doubleValue();
			}
		}
        if (valueMeasureUnitCode != null || valueMeasure != null){
            UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(valueMeasureUnitCode);
            if (unitCodeEnum != null) {
                BigDecimal quantity = new BigDecimal(valueMeasure);
                BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
                calculatedValueMeasure =  result.doubleValue();
            }
        }
	}
}
