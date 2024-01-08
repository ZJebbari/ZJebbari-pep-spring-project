package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account register(Account account){
        
        if(accountRepository.findByUsername(account.getUsername()).isPresent()){
            return null;
        }
        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }

    public Account login(Account account){
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());
        if(existingAccount.isPresent() && existingAccount.get().getPassword().equals(account.getPassword())){
            return existingAccount.get();
        }
        return null;
    }

    public Account findById(Integer id){
        return accountRepository.findById(id).orElseThrow();
    }
    

}
