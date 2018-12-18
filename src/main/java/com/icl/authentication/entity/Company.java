package com.icl.authentication.entity;

import com.icl.authentication.entity.enumber.CompanyStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@ToString(callSuper = true)
public class Company extends BaseEntity {
    private String companyName;
    @Enumerated(EnumType.STRING)
    private CompanyStatus status;
    private Boolean deleted;
}
