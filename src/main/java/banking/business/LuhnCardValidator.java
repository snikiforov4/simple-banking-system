package banking.business;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class LuhnCardValidator implements ICardValidator {

    @Override
    public boolean isCardNumberValid(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return false;
        }
        return calculateCheckSum(cardNumber) == cardNumber.charAt(cardNumber.length() - 1);
    }

    @Override
    public  char calculateCheckSum(String cardNumber) {
        Preconditions.checkNotNull(cardNumber);
        Preconditions.checkArgument(cardNumber.length() == 15 || cardNumber.length() == 16,
                "wrong number of digits in card number: %s", cardNumber);
        int[] array = toArrayOfDigits(cardNumber);
        updateArray(array);
        int sum = Arrays.stream(array).sum();
        int checksumDigit = (10 - (sum % 10)) % 10;
        return Character.forDigit(checksumDigit, 10);
    }

    private int[] toArrayOfDigits(String number) {
        int[] result = new int[15];
        for (int i = 0; i < result.length; i++) {
            result[i] = Character.digit(number.charAt(i), 10);
        }
        return result;
    }

    private void updateArray(int[] array) {
        for (int i = 0; i < array.length; i += 2) {
            int value = array[i];
            value *= 2;
            if (value > 9) {
                value -= 9;
            }
            array[i] = value;
        }
    }
}
