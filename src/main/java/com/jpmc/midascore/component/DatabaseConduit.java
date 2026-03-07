package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    // Task 4
    private final IncentiveService incentiveService;

    /*
    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
    */
    // Task 4
    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        // Find sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Validate both sender and recipient exist
        if (sender == null || recipient == null) {
            return false;
        }

        // Validate: sender balance >= transaction amount
        if (sender.getBalance() < transaction.getAmount()) {
            return false;
        }

        // Get incentive from external API
        float incentiveAmount = incentiveService.getIncentive(transaction);

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        // recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Task 4: incentive added to recipient only, not deducted from sender
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Persist to database
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount));

        return true;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    // Task 5
    public float queryUserBalance(Long userId) {
        UserRecord user = userRepository.findById(userId.longValue());
        if (user == null) {
            return 0;
        }
        return user.getBalance();
    }
}