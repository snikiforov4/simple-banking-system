package banking.config;

import banking.ArgsHelper;
import banking.business.CardService;
import banking.business.ICardValidator;
import banking.business.LuhnCardValidator;
import banking.persistence.CardRepository;
import banking.persistence.DbService;
import banking.persistence.ICardRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

public class BaseModule extends AbstractModule {

    private final String[] args;

    public BaseModule(String[] args) {
        this.args = args;
    }

    @Provides
    @DbPath
    String dbPathName() {
        return ArgsHelper.getDbPath(args);
    }

    @Provides
    @Singleton
    DbService dbService(@DbPath String dbPath) {
        return new DbService(dbPath);
    }

    @Provides
    @Singleton
    ICardRepository cardRepository(DbService dbService) {
        return new CardRepository(dbService);
    }

    @Provides
    @Singleton
    CardService cardService(ICardValidator cardValidator, ICardRepository cardRepository) {
        return new CardService(cardValidator, cardRepository);
    }

    @Provides
    @Singleton
    ICardValidator cardValidator() {
        return new LuhnCardValidator();
    }
}
