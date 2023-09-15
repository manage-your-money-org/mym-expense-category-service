package com.rkumar0206.mymexpensecategoryservice.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String name;
    private String emailId;
    private String uid;
    private boolean isAccountVerified;
}
