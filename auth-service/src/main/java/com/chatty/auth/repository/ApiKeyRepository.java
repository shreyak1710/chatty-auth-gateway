
package com.chatty.auth.repository;

import com.chatty.auth.model.ApiKey;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {
    List<ApiKey> findByCustomerId(String customerId);
    Optional<ApiKey> findByApiKey(String apiKey);
    boolean existsByKeyNameAndCustomerId(String keyName, String customerId);
}
