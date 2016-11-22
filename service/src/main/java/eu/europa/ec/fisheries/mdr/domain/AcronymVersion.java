package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by kovian on 10/11/2016.
 */
@Entity
@Table(name = "mdr_acronymversion")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AcronymVersion extends BaseEntity {

    @Column(name = "version_name")
    private String versionName;

    @Embedded
    private DateRange validity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_ref_id", nullable = false)
    //@Cascade(CascadeType.ALL)
    private MdrCodeListStatus mdrCodeListStatus;

    public AcronymVersion() {
        super();
    }

    public AcronymVersion(String versionName, DateRange validity) {
        this.versionName = versionName;
        this.validity = validity;
    }

    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    public DateRange getValidity() {
        return validity;
    }
    public void setValidity(DateRange validity) {
        this.validity = validity;
    }
    public MdrCodeListStatus getMdrCodeListStatus() {
        return mdrCodeListStatus;
    }
    public void setMdrCodeListStatus(MdrCodeListStatus mdrCodeListStatus) {
        this.mdrCodeListStatus = mdrCodeListStatus;
    }
}