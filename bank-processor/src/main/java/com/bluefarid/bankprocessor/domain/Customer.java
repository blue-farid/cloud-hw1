package com.bluefarid.bankprocessor.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer {
    @Id
    private Long id;
    private String email;
    private String lastname;
    private String nationalId;
    private String ip;
    private String image1;
    private String image2;

    @Enumerated(EnumType.STRING)
    private CustomerState state;
    @Column(unique = true)
    private String username;
}
