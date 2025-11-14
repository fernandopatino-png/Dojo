package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;

public interface AccountEventListener {

    void onAccountCreated(Account account);

    void onBalanceChanged(Account account, Double oldBalance, Double newBalance);

    void onAccountDeleted(Long accountId);
}
