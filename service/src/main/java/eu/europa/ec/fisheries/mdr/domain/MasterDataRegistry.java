package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.domain.Audit;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.CharBooleanConverter;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString
public class MasterDataRegistry extends BaseEntity {

	@Convert(converter = CharBooleanConverter.class)
    @Column(nullable = true, length = 1)
    private Boolean refreshable;

    @Embedded
    private Audit audit;
    
    @Embedded
    private DateRange validity;
    
    @PrePersist
    private void onCreate() {
        audit = new Audit(DateUtils.nowUTC().toDate());
    }

    public Boolean getRefreshable() {
        return refreshable;
    }

    public void setRefreshable(Boolean refreshable) {
        this.refreshable = refreshable;
    }

    public Audit getAudit() {
        return audit;
    }

}
