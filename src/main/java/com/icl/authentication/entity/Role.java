package com.icl.authentication.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Role extends BaseEntity {
    private Long applicationId;
    private String name;
    private Boolean deleted;
}
