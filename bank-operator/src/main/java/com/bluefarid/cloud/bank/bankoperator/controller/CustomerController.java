package com.bluefarid.cloud.bank.bankoperator.controller;

import com.bluefarid.cloud.bank.bankoperator.dto.CustomerDto;
import com.bluefarid.cloud.bank.bankoperator.exception.IpNotEqualException;
import com.bluefarid.cloud.bank.bankoperator.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping
    public ResponseEntity<CustomerDto> saveCustomer(@RequestBody CustomerDto dto, HttpServletRequest request) {
        return ResponseEntity.ok(customerService.saveCustomer(dto.setIp(request.getRemoteAddr())));
    }

    @PostMapping("/{username}/image/{index}")
    public ResponseEntity<String> uploadImage(@RequestBody MultipartFile file, @PathVariable String username, @PathVariable String index) {
        try {
            customerService.uploadImage(file, username, index);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/{nationalId}")
    public ResponseEntity<String> getCustomerStateString(@PathVariable String nationalId, HttpServletRequest request) {
        if (!request.getRemoteAddr().equals(customerService.getCustomerIp(nationalId)))
            throw new IpNotEqualException("IP changed!");

        return ResponseEntity.ok(customerService.getCustomerStateString(nationalId));
    }
}
