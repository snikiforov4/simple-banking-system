package banking.persistence;

import banking.Card;

import java.util.Optional;

public interface ICardRepository {
    Optional<Card> findByCardNumber(String cardNumber);

    Card save(Card card);

    void deleteByCardNumber(String cardNumber);

    long updateBalance(String cardNumber, long income);

    long transfer(String fromCardNumber, String toCardNumber, long delta);
}
