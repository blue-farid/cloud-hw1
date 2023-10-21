package com.bluefarid.cloud.bank.bankoperator.service;

import com.bluefarid.cloud.bank.bankoperator.dto.CustomerDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CustomerService {
    CustomerDto saveCustomer(CustomerDto dto);
    String getCustomerStateString(String nationalId);

    String getCustomerIp(String nationalId);

    void uploadImage(MultipartFile file, String username, String index) throws IOException;
}
