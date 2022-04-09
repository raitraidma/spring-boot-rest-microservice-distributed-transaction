package io.github.raitraidma.microservice.account.service;

import io.github.raitraidma.microservice.account.dto.AccountOrderDto;
import io.github.raitraidma.microservice.account.model.Account;
import io.github.raitraidma.microservice.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountOrderDto withdrawBalance(Long accountId, Long amount) {
        Account account = accountRepository.getById(accountId);
        account.setBalance(account.getBalance() - amount);

        account = accountRepository.save(account);

        AccountOrderDto accountOrderDto = new AccountOrderDto();
        accountOrderDto.setId(account.getId());
        return accountOrderDto;
    }
}
