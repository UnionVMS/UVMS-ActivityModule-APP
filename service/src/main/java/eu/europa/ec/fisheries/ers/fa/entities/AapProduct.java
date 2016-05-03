package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_aap_product")
public class AapProduct implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_process_id")
	private AapProcess aapProcess;
	
	@Column(name = "packaging_type_code")
	private String packagingTypeCode;
	
	@Column(name = "packaging_type_code_list_id")
	private String packagingTypeCodeListId;
	
	@Column(name = "packaging_unit_avarage_weight", precision = 17, scale = 17)
	private Double packagingUnitAvarageWeight;
	
	@Column(name = "packaging_unit_count", precision = 17, scale = 17)
	private Double packagingUnitCount;

	public AapProduct() {
	}

	public AapProduct(int id) {
		this.id = id;
	}

	public AapProduct(int id, AapProcess aapProcess, String packagingTypeCode,
			String packagingTypeCodeListId, Double packagingUnitAvarageWeight,
			Double packagingUnitCount) {
		this.id = id;
		this.aapProcess = aapProcess;
		this.packagingTypeCode = packagingTypeCode;
		this.packagingTypeCodeListId = packagingTypeCodeListId;
		this.packagingUnitAvarageWeight = packagingUnitAvarageWeight;
		this.packagingUnitCount = packagingUnitCount;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public AapProcess getAapProcess() {
		return this.aapProcess;
	}

	public void setAapProcess(AapProcess aapProcess) {
		this.aapProcess = aapProcess;
	}

	
	public String getPackagingTypeCode() {
		return this.packagingTypeCode;
	}

	public void setPackagingTypeCode(String packagingTypeCode) {
		this.packagingTypeCode = packagingTypeCode;
	}

	
	public String getPackagingTypeCodeListId() {
		return this.packagingTypeCodeListId;
	}

	public void setPackagingTypeCodeListId(String packagingTypeCodeListId) {
		this.packagingTypeCodeListId = packagingTypeCodeListId;
	}


	public Double getPackagingUnitAvarageWeight() {
		return this.packagingUnitAvarageWeight;
	}

	public void setPackagingUnitAvarageWeight(Double packagingUnitAvarageWeight) {
		this.packagingUnitAvarageWeight = packagingUnitAvarageWeight;
	}

	
	public Double getPackagingUnitCount() {
		return this.packagingUnitCount;
	}

	public void setPackagingUnitCount(Double packagingUnitCount) {
		this.packagingUnitCount = packagingUnitCount;
	}

}
