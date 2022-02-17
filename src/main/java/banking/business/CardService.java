package banking.business;

import banking.Card;
import banking.persistence.ICardRepository;
import com.google.common.base.Preconditions;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class CardService {

    private static final String CARD_BIN = "400000";

    private final ICardValidator cardValidator;
    private final ICardRepository cardRepository;
    private final Random random;

    public CardService(ICardValidator cardValidator, ICardRepository cardRepository) {
        this.cardValidator = cardValidator;
        this.cardRepository = cardRepository;
        this.random = new SecureRandom();
    }

    public Card issueCard() {
        String pin = generateRandomPin();
        String cardNumber = generateUniqueCardNumber();
        Card card = new Card();
        card.setPin(pin);
        card.setNumber(cardNumber);
        return cardRepository.save(card);
    }

    private String generateRandomPin() {
        return String.format("%04d", random.nextInt(10000));
    }

    private String generateUniqueCardNumber() {
        String accountIdentifier = String.format("%09d", random.nextLong(1_000_000_000L));
        StringBuilder builder = new StringBuilder().append(CARD_BIN).append(accountIdentifier);
        char checkSum = cardValidator.calculateCheckSum(builder.toString());
        return builder.append(checkSum).toString();
    }

    public boolean exists(final String cardNumber) {
        Objects.requireNonNull(cardNumber);
        return cardRepository.findByCardNumber(cardNumber).isPresent();
    }

    public Optional<Card> getCard(final String cardNumber, final String cardPin) {
        Objects.requireNonNull(cardNumber);
        Objects.requireNonNull(cardPin);
        return cardRepository.findByCardNumber(cardNumber)
                .filter(card -> Objects.equals(card.getPin(), cardPin));
    }

    public void deleteCard(final String cardNumber) {
        Objects.requireNonNull(cardNumber);
        cardRepository.deleteByCardNumber(cardNumber);
    }

    public long addIncome(final String cardNumber, final long income) {
        Preconditions.checkArgument(income > 0, "Income: %s", income);
        return cardRepository.updateBalance(cardNumber, income);
    }

    public long transfer(final String fromCardNumber,
                         final String toCardNumber,
                         final long delta) {
        return cardRepository.transfer(fromCardNumber, toCardNumber, delta);
    }
}
