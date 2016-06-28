package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "activity_size_distribution")
public class SizeDistributionEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "class_code", nullable = false)
	private String classCode;

	@Column(name = "class_code_list_id", nullable = false)
	private String classCodeListId;

	@Column(name = "category_code")
	private String categoryCode;

	@Column(name = "category_code_list_id")
	private String categoryCodeListId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "sizeDistribution")
	private FaCatchEntity faCatch;

	public SizeDistributionEntity() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClassCode() {
		return this.classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getClassCodeListId() {
		return this.classCodeListId;
	}

	public void setClassCodeListId(String classCodeListId) {
		this.classCodeListId = classCodeListId;
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryCodeListId() {
		return this.categoryCodeListId;
	}

	public void setCategoryCodeListId(String categoryCodeListId) {
		this.categoryCodeListId = categoryCodeListId;
	}

	public FaCatchEntity getFaCatch() {
		return faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}
}
