package banking;

import banking.config.BaseModule;
import banking.persistence.DbService;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws TimeoutException {
        Injector injector = Guice.createInjector(new BaseModule(args));
        injector.getInstance(DbService.class).startAsync().awaitRunning(Duration.ofSeconds(5));
        injector.getInstance(MainActivity.class).run();
        injector.getInstance(DbService.class).stopAsync().awaitTerminated(Duration.ofSeconds(5));
    }
}