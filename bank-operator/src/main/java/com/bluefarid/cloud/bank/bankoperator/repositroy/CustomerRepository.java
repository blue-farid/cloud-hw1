package com.bluefarid.cloud.bank.bankoperator.repositroy;

import com.bluefarid.cloud.bank.bankoperator.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNationalId(String nationalId);
    boolean existsByUsername(String username);
    Optional<Customer> findByUsername(String username);
}
