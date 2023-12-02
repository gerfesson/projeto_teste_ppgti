package com.panacademy.squad7.bluebank.services.impl;

import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.Subscription;
import com.panacademy.squad7.bluebank.domain.enums.StatusType;
import com.panacademy.squad7.bluebank.domain.models.Account;
import com.panacademy.squad7.bluebank.domain.models.Transaction;
import com.panacademy.squad7.bluebank.domain.repositories.AccountsRepository;
import com.panacademy.squad7.bluebank.domain.repositories.TransactionsRepository;
import com.panacademy.squad7.bluebank.exceptions.ContentNotFoundException;
import com.panacademy.squad7.bluebank.exceptions.InvalidInputException;
import com.panacademy.squad7.bluebank.services.TransactionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionsServiceImpl implements TransactionsService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsServiceImpl.class);

    private final TransactionsRepository transactionsRepository;
    private final AccountsRepository accountsRepository;


    public TransactionsServiceImpl(
                                   TransactionsRepository transactionsRepository,
                                   AccountsRepository accountsRepository
                                    ) {
       // TOPIC_ARN = topicArn;
        this.transactionsRepository = transactionsRepository;
        this.accountsRepository = accountsRepository;

    }

    @Override
    public Transaction deposit(Transaction transaction) {
        Transaction transactionSaved = transactionsRepository.save(transaction);
        Account destinationAccount = transaction.getDestinationAccount();
        destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));
        transactionSaved.setDestinationAccount(accountsRepository.save(destinationAccount));

        return transactionSaved;
    }

    @Override
    public Transaction withdraw(Transaction transaction) {
        Account originAccount = transaction.getOriginAccount();
        if (originAccount.getBalance().compareTo(transaction.getAmount()) <= 0) {
            throw new InvalidInputException("the " + originAccount.getType() + " account " + originAccount.getAccountNumber() + " does not have enough balance");
        }
        Transaction transactionSaved = transactionsRepository.save(transaction);
        originAccount.setBalance(originAccount.getBalance().subtract(transaction.getAmount()));
        transactionSaved.setOriginAccount(accountsRepository.save(originAccount));

        return transactionSaved;
    }

    @Override
    public Transaction transfer(Transaction transaction) {
        Account originAccount = transaction.getOriginAccount();
        Account destinationAccount = transaction.getDestinationAccount();
        if (originAccount.getBalance().compareTo(transaction.getAmount()) <= 0) {
            throw new InvalidInputException("the origin " + originAccount.getType() + " account " + originAccount.getAccountNumber() + " does not have enough balance");
        }
        if (!destinationAccount.getStatus().equals(StatusType.A)) {
            throw new InvalidInputException("the destination " + destinationAccount.getType() + " account " + destinationAccount.getAccountNumber() + " is not active");
        }
        Transaction transactionSaved = transactionsRepository.save(transaction);
        originAccount.setBalance(originAccount.getBalance().subtract(transaction.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));
        transactionSaved.setOriginAccount(accountsRepository.save(originAccount));
        transactionSaved.setDestinationAccount(accountsRepository.save(destinationAccount));

        return transactionSaved;
    }

    @Override
    public Transaction findById(Long id) {
        return transactionsRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("transaction not found with id " + id));
    }

    @Override
    public List<Transaction> findAll() {
        return transactionsRepository.findAll();
    }



    private String buildDebitMessage(Transaction transaction) {
        Account origin = transaction.getOriginAccount();
        Account destination = transaction.getDestinationAccount();

        String message = "Hello " + origin.getClient().getName() + ",\n\n";
        message += "You made a " + transaction.getType() + " of $" + transaction.getAmount();
        message += ", from your " + origin.getType() + " account " + origin.getAccountNumber() + " of agency " + origin.getAgencyNumber();

        if (destination != null) {
            message += ", to account " + destination.getAccountNumber() + " of agency " + destination.getAgencyNumber();
        }
        message += ".\n";
        return message;
    }

    private String buildCreditMessage(Transaction transaction) {
        Account origin = transaction.getOriginAccount();
        Account destination = transaction.getDestinationAccount();

        String message = "Hello " + destination.getClient().getName() + ",\n\n";
        message += "You received a " + transaction.getType() + " of $" + transaction.getAmount();
        message += ", into your " + destination.getType() + " account " + destination.getAccountNumber() + " of agency " + destination.getAgencyNumber();

        if (origin != null) {
            message += ", from account " + origin.getAccountNumber() + " of agency " + origin.getAgencyNumber();
        }
        message += ".\n";
        return message;
    }
}
