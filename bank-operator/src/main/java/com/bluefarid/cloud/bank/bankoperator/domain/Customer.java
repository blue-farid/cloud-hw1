package com.bluefarid.cloud.bank.bankoperator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
