package com.bluefarid.cloud.bank.bankoperator.service;

import com.bluefarid.cloud.bank.bankoperator.domain.Customer;
import com.bluefarid.cloud.bank.bankoperator.domain.CustomerState;
import com.bluefarid.cloud.bank.bankoperator.dto.CustomerDto;
import com.bluefarid.cloud.bank.bankoperator.exception.CustomerNotFoundException;
import com.bluefarid.cloud.bank.bankoperator.integration.RabbitMqProducer;
import com.bluefarid.cloud.bank.bankoperator.integration.s3.ObjectStorage;
import com.bluefarid.cloud.bank.bankoperator.mapper.CustomerMapper;
import com.bluefarid.cloud.bank.bankoperator.repositroy.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final RabbitMqProducer rabbit;
    private final ObjectStorage objectStorage;

    @Override
    public CustomerDto saveCustomer(CustomerDto dto) {
        CustomerDto res = mapper
                .toDto(repository
                        .save(mapper.toEntity(dto
                                        .setUsername(dto.getUsername()))
                                .setNationalId(hashString(dto.getNationalId()))));
        res.setUsername(generateUsername(res.getEmail(), res.getId().toString()));
        res.setState(CustomerState.PENDING);
        repository.save(mapper.toEntity(res));
        return res;
    }

    @Override
    public String getCustomerStateString(String nationalId) {
        Optional<Customer> customer = repository.findByNationalId(hashString(nationalId));
        if (customer.isEmpty())
            throw new CustomerNotFoundException("Customer not found!");
        return stateToString(customer.get());
    }

    @Override
    public String getCustomerIp(String nationalId) {
        Optional<Customer> customer = repository.findByNationalId(hashString(nationalId));
        if (customer.isEmpty())
            throw new CustomerNotFoundException("Customer not found!");
        return customer.get().getIp();
    }

    private String stateToString(Customer customer) {
        return switch (customer.getState()) {
            case ACCEPT -> "احراز هویت شما با موفیت انجام شد. نام کاربری شما".concat(customer.getUsername())
                    .concat("است.");
            case REJECT -> "درخواست شما رد شد.";
            case PENDING -> "درخواست شما درحال بررسی است.";

        };
    }

    private String generateUsername(String mail, String id) {
        return mail.concat(id);
    }

    private static String hashString(String input) {
        try {
            // Create an instance of the SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convert the input string to bytes
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception if the algorithm is not available
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void uploadImage(MultipartFile file, String username, String index) throws IOException {
        Optional<Customer> customer = repository.findByUsername(username);
        if (customer.isEmpty())
            throw new CustomerNotFoundException("customer not found!");

        String key = index.concat("-").concat(username);

        if (index.equals("1"))
            customer.get().setImage1(key);
        else
            customer.get().setImage2(key);

        repository.save(customer.get());

        objectStorage.uploadFile("bank-processor",
                key, file.getBytes());

        if(!index.equals("1"))
            rabbit.produceMessage(username);
    }
}
