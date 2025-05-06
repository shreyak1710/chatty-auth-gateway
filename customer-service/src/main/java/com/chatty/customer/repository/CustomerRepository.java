
package com.chatty.customer.repository;

import com.chatty.customer.model.CustomerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerProfile, String> {
    Optional<CustomerProfile> findByCustomerId(String customerId);
    Optional<CustomerProfile> findByCustomerEmail(String email);
    boolean existsByCustomerEmail(String email);
}
