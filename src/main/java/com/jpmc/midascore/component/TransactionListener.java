package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionListener {

    private final DatabaseConduit databaseConduit;
    private final UserRepository userRepository;

    public TransactionListener(DatabaseConduit databaseConduit, UserRepository userRepository) {
        this.databaseConduit = databaseConduit;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction: " + transaction.getAmount());
        databaseConduit.processTransaction(transaction);
        /*
        for (UserRecord user : userRepository.findAll()) {
            if (user.getName().equals("waldorf")) {
                System.out.println("WALDORF BALANCE: " + user.getBalance());
            }
        }
        */
    }
}
