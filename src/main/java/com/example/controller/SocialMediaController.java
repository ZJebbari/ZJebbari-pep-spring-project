package com.example.controller;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpSession;

import java.util.*;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account){
       if (account.getUsername() == null || account.getUsername().isBlank() || account.getPassword() == null || account.getPassword().length() < 4){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be blank and password must be at least 4 characters long.");
       }
       Account registeredAccount = accountService.register(account);
       if(registeredAccount == null){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
       }
       return ResponseEntity.ok(registeredAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account, HttpSession session){
    Account existingAccount = accountService.login(account);
    if(existingAccount !=null){
        // Set user ID in session upon successful login
        session.setAttribute("userId", existingAccount.getAccount_id());
        return ResponseEntity.ok(existingAccount);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
}

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            return ResponseEntity.ok(createdMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<?> listOfMessages(){
        List<Message> messages = messageService.retrieveAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> messageById(@PathVariable Integer messageId){
    Optional<Message> messageOpt = messageService.retrieveMessageById(messageId);
    if(messageOpt.isPresent()){
        return ResponseEntity.ok(messageOpt.get());
    } else {
        return ResponseEntity.ok(null); 
    }
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessageByMessageId(@PathVariable Integer messageId){
    String value = messageService.deleteMessageByIdMessage(messageId);
         return ResponseEntity.ok(value);
    }

    @PatchMapping(value ="/messages/{messageId}")
    public ResponseEntity<String> updateMessage(@RequestBody(required = false) String messageText, @PathVariable Integer messageId){
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(messageText);
        if(jsonNode.has("message_text")) {
            messageText = jsonNode.get("message_text").asText();
        }
    } catch (Exception e) {
       
    }
    Optional<Message> messageExist = messageService.retrieveMessageById(messageId);
    if(messageExist.isPresent() && !messageText.isBlank() && messageText.length() <= 255 ){
         messageService.updateMessage(messageText, messageId);
        return ResponseEntity.ok("1");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
}

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<?> getAllMessagesOfAccount(@PathVariable Integer accountId){
        List<Message> messages = messageService.retrieveAllMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages);
    }

}