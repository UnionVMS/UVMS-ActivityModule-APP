package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_aap_stock")
public class AapStockEntity implements Serializable {

	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;
	
	@Column(name = "stock_id")
	private String stockId;
	
	@Column(name = "stock_scheme_id")
	private String stockSchemeId;

	public AapStockEntity() {
	}

	public AapStockEntity(int id) {
		this.id = id;
	}

	public AapStockEntity(int id, FaCatchEntity faCatch,
						  String stockId, String stockSchemeId) {
		this.id = id;
		this.faCatch = faCatch;
		this.stockId = stockId;
		this.stockSchemeId = stockSchemeId;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FaCatchEntity getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}

	
	public String getStockId() {
		return this.stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	
	public String getStockSchemeId() {
		return this.stockSchemeId;
	}

	public void setStockSchemeId(String stockSchemeId) {
		this.stockSchemeId = stockSchemeId;
	}

}
