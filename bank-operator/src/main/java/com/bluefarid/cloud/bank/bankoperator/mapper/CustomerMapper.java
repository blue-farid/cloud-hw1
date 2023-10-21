package com.bluefarid.cloud.bank.bankoperator.mapper;

import com.bluefarid.cloud.bank.bankoperator.domain.Customer;
import com.bluefarid.cloud.bank.bankoperator.dto.CustomerDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDto toDto(Customer customer) {
        return new CustomerDto().setIp(customer.getIp()).
                setId(customer.getId())
                .setEmail(customer.getEmail())
                .setLastname(customer.getLastname())
                .setImage1(customer.getImage1())
                .setImage2(customer.getImage2())
                .setState(customer.getState())
                .setNationalId(customer.getNationalId())
                .setUsername(customer.getUsername());
    }

    public Customer toEntity(CustomerDto dto) {
        return new Customer().setIp(dto.getIp()).
                setId(dto.getId())
                .setEmail(dto.getEmail())
                .setLastname(dto.getLastname())
                .setImage1(dto.getImage1())
                .setImage2(dto.getImage2())
                .setState(dto.getState())
                .setNationalId(dto.getNationalId())
                .setUsername(dto.getUsername());    }
}
