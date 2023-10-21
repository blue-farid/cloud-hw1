package com.bluefarid.cloud.bank.bankoperator.dto;

import com.bluefarid.cloud.bank.bankoperator.domain.CustomerState;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerDto {
    private Long id;
    private String email;
    private String lastname;
    private String nationalId;
    private String ip;
    private String image1;
    private String image2;
    private CustomerState state;
    private String username;
}
