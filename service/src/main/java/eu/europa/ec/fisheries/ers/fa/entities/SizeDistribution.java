package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "activity_size_distribution")
public class SizeDistribution {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "class_code", nullable = false)
	private String classCode;

	@Column(name = "class_code_list_id", nullable = false)
	private String classCodeListId;

	@Column(name = "category_code")
	private String categoryCode;

	@Column(name = "category_code_list_id")
	private String categoryCodeListId;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "SizeDistribution", cascade = CascadeType.ALL)
	private FaCatch faCatchs;

	public SizeDistribution() {
	}

	public SizeDistribution(int id, String classCode, String classCodeListId) {
		this.id = id;
		this.classCode = classCode;
		this.classCodeListId = classCodeListId;
	}

	public SizeDistribution(int id, String classCode, String classCodeListId,
			String categoryCode, String categoryCodeListId,
			FaCatch faCatchs) {
		this.id = id;
		this.classCode = classCode;
		this.classCodeListId = classCodeListId;
		this.categoryCode = categoryCode;
		this.categoryCodeListId = categoryCodeListId;
		this.faCatchs = faCatchs;
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

	public FaCatch getFaCatchs() {
		return faCatchs;
	}

	public void setFaCatchs(FaCatch faCatchs) {
		this.faCatchs = faCatchs;
	}

}
