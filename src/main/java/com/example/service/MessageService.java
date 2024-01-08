package com.example.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository , AccountRepository accountRepository){
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Message submitMessage(Message message){
        Message savedMessage = messageRepository.save(message);
        return savedMessage;
    }

    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            throw new IllegalArgumentException("Message text must not be blank and should not exceed 255 characters.");
        }

        if (accountRepository.existsById(message.getPosted_by())) {
            return messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Posted_by user does not exist.");
        }
    }

    public List<Message> retrieveAllMessages(){
       return messageRepository.findAll();
    }

    public Optional<Message> retrieveMessageById(Integer id){
        return messageRepository.findById(id);
    }

    public String deleteMessageByIdMessage(Integer id){
        if (messageRepository.existsById(id)){
            messageRepository.deleteById(id);
            return "1";
        }
        return "";
    }

    public Message updateMessage(String messageText, Integer id){
        
        Optional<Message> messageOpt = messageRepository.findById(id);
        
            Message message = messageOpt.get();
            message.setMessage_text(messageText);
            return messageRepository.save(message);
        
    }
    
    
    public List<Message> retrieveAllMessagesByAccountId(Integer accountId){
        List<Message> messages = messageRepository.findAll();
        
        List<Message> filteredMessages = messages.stream()
            .filter(message -> accountId.equals(message.getMessage_id()))
             .collect(Collectors.toList());
    
        return filteredMessages;
    }
    


}
