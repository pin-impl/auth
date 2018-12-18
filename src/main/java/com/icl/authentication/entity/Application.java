package com.icl.authentication.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class Application extends BaseEntity {
    private String name;
    private Long companyId;
    private Boolean deleted;
}
