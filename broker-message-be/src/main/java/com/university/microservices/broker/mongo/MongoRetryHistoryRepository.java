package com.university.microservices.broker.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRetryHistoryRepository extends MongoRepository<MongoRetryHistory, String> {
}
