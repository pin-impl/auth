package com.icl.authentication.entity;

import com.icl.authentication.entity.enumber.Gender;
import com.icl.authentication.entity.enumber.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@ToString(callSuper = true)
public class User extends BaseEntity {
    private String userName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Long companyId;
    private String phone;
    private String email;
    private String password;
    private UserStatus status;
    private Boolean deleted;
}
