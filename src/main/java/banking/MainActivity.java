package banking;

import banking.business.CardService;
import banking.business.ICardValidator;
import com.google.inject.Inject;

import static banking.ConsoleHelper.*;

public class MainActivity {

    private final CardService cardService;
    private final ICardValidator cardValidator;

    @Inject
    public MainActivity(CardService cardService, ICardValidator cardValidator) {
        this.cardService = cardService;
        this.cardValidator = cardValidator;
    }

    public void run() {
        while (true) {
            printMenu();
            int input = tryReadInt().orElse(-1);
            println();
            switch (input) {
                case 0 -> {
                    println("Bye!");
                    return;
                }
                case 1 -> {
                    Card card = cardService.issueCard();
                    printCardInfo(card);
                    println();
                }
                case 2 -> {
                    println("Enter your card number:");
                    String cardNumber = read();
                    println("Enter your PIN:");
                    String cardPin = read();
                    println();
                    Card card = cardService.getCard(cardNumber, cardPin).orElse(null);
                    if (card == null) {
                        println("Wrong card number or PIN!");
                        continue;
                    }
                    println("You have successfully logged in!");
                    userLoop:
                    while (true) {
                        printCardMenu();
                        int cardInput = tryReadInt().orElse(-1);
                        println();
                        switch (cardInput) {
                            case 0 -> {
                                println("Bye!");
                                return;
                            }
                            case 1 -> {
                                println("Balance: " + card.getBalance());
                                println();
                            }
                            case 2 -> {
                                println("Enter income:");
                                tryReadInt().ifPresentOrElse(
                                        income -> {
                                            if (income <= 0) {
                                                println("Income should be positive!");
                                            } else {
                                                long newBalance = cardService.addIncome(card.getNumber(), income);
                                                card.setBalance(newBalance);
                                                println("Income was added!");
                                            }
                                        },
                                        () -> println("Wrong input!")
                                );
                                println();
                            }
                            case 3 -> {
                                println("Transfer");
                                println("Enter card number:");
                                String toCardNumber = read();
                                if (!cardValidator.isCardNumberValid(toCardNumber)) {
                                    println("Probably you made a mistake in the card number. Please try again!");
                                    continue;
                                }
                                if (card.getNumber().equals(toCardNumber)) {
                                    println("You can't transfer money to the same account!");
                                    continue;
                                }
                                if (!cardService.exists(toCardNumber)) {
                                    println("Such a card does not exist.");
                                    continue;
                                }
                                println("Enter how much money you want to transfer:");
                                long delta = Long.parseLong(read());
                                if (card.getBalance() - delta < 0) {
                                    println("Not enough money!");
                                    continue;
                                }
                                long newBalance = cardService.transfer(card.getNumber(), toCardNumber, delta);
                                card.setBalance(newBalance);
                                println("Success!");
                                println();
                            }
                            case 4 -> {
                                cardService.deleteCard(card.getNumber());
                                println("The account has been closed!");
                                println();
                                break userLoop;
                            }
                            case 5 -> {
                                println("You have successfully logged out!");
                                println();
                                break userLoop;
                            }
                            default -> println("Wrong input!");
                        }

                    }
                }
                default -> println("Wrong input!");
            }
        }
    }

    private void printMenu() {
        println("1. Create an account");
        println("2. Log into account");
        println("0. Exit");
    }

    private void printCardInfo(Card card) {
        println("Your card has been created");
        println("Your card number:");
        println(card.getNumber());
        println("Your card PIN:");
        println(card.getPin());
    }

    private void printCardMenu() {
        println("1. Balance");
        println("2. Add income");
        println("3. Do transfer");
        println("4. Close account");
        println("5. Log out");
        println("0. Exit");
    }
}
