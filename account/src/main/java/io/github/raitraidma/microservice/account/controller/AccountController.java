package io.github.raitraidma.microservice.account.controller;

import io.github.raitraidma.microservice.account.dto.AccountOrderDto;
import io.github.raitraidma.microservice.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("{accountId}/withdraw")
    public AccountOrderDto withdraw(@PathVariable Long accountId, @RequestParam Long amount) {
        return accountService.withdrawBalance(accountId, amount);
    }
}
