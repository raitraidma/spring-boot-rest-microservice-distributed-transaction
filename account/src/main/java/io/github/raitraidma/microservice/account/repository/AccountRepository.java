package io.github.raitraidma.microservice.account.repository;

import io.github.raitraidma.microservice.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
