package banking.business;

public interface ICardValidator {

    boolean isCardNumberValid(String cardNumber);

    char calculateCheckSum(String cardNumber);
}
