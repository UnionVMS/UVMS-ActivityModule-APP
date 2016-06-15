package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_aap_process")
public class AapProcessEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;
	
	@Column(name = "conversion_factor")
	private Integer conversionFactor;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aapProcess", cascade = CascadeType.ALL)
	private Set<AapProductEntity> aapProducts = new HashSet<AapProductEntity>(
			0);

	public AapProcessEntity() {
	}

	public AapProcessEntity(String typeCode, String typeCodeListId) {
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
	}

	public AapProcessEntity(FaCatchEntity faCatch,
							String typeCode, String typeCodeListId, Integer conversionFactor,
							Set<AapProductEntity> aapProducts) {
		this.faCatch = faCatch;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.conversionFactor = conversionFactor;
		this.aapProducts = aapProducts;
	}

	
	public int getId() {
		return this.id;
	}


	public FaCatchEntity getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}

	
	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	
	public String getTypeCodeListId() {
		return this.typeCodeListId;
	}

	public void setTypeCodeListId(String typeCodeListId) {
		this.typeCodeListId = typeCodeListId;
	}

	
	public Integer getConversionFactor() {
		return this.conversionFactor;
	}

	public void setConversionFactor(Integer conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	
	public Set<AapProductEntity> getAapProducts() {
		return this.aapProducts;
	}

	public void setAapProducts(
			Set<AapProductEntity> aapProducts) {
		this.aapProducts = aapProducts;
	}

}
