/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.domain.Audit;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.CharBooleanConverter;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.search.annotations.*;
import xeu.ec.fisheries.flux_bl.flux_aggregatebusinessinformationentity._1._1.FLUXPeriodType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.CodeElementType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import java.util.List;

@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString
@Indexed
abstract public class MasterDataRegistry extends BaseEntity {

    @Convert(converter = CharBooleanConverter.class)
    @Column(nullable = true, length = 1)
    @Field(name="refreshable", analyze= Analyze.NO)
  //  @SortableField
    private Boolean refreshable;

    @Embedded
    private Audit audit;

    @Embedded
    private DateRange validity;

    public Boolean getRefreshable() {
        return refreshable;
    }

    public void setRefreshable(Boolean refreshable) {
        this.refreshable = refreshable;
    }

    public Audit getAudit() {
        return audit;
    }

    public void createAudit() {
        audit = new Audit(DateUtils.nowUTC().toDate());
    }

    public DateRange getValidity() {
        return validity;
    }

    public void setValidity(DateRange validity) {
        this.validity = validity;
    }

    public abstract String getAcronym();

    public void populateFromJAXBFields(CodeElementType actualJaxbElement) throws FieldNotMappedException {
        FLUXPeriodType jaxbValidity = actualJaxbElement.getValidityPeriod();
        if (jaxbValidity != null) {
            this.setValidity(
                    new DateRange(jaxbValidity.getStartDate().toGregorianCalendar().getTime(),
                                  jaxbValidity.getEndDate().toGregorianCalendar().getTime()));
        }
        populate(actualJaxbElement.getFields());
    }

    public void populate(List<FieldType> fields) throws FieldNotMappedException {};

    /*/**
     * This scannes the current class for all Lucene indexed class members.
     * @return an array of the names of all indexed class members.
     *
    public String[] getAllSearchableFields() {
        Class thisClass = this.getClass();
        java.lang.reflect.Field[] allFields = thisClass.getFields();
        Collection<String> indexedFields = null;

        if (ArrayUtils.isNotEmpty(allFields)){
            indexedFields = new ArrayList<>();

            for(java.lang.reflect.Field classMember: allFields ) {
                Annotation[] annotations = classMember.getAnnotations();
                for(Annotation annotation:annotations) {
                    if (annotation instanceof Fields) {
                        Field[] subAnnotations = ((Fields) annotation).value();
                        for(Field field: allFields) {
                            if (field )//is indexed
                        }
                    } else // if it is Field and indexed
                }
            }
        }

        return indexedFields;
    }
    */


}