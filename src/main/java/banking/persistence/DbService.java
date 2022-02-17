package banking.persistence;

import com.google.common.util.concurrent.AbstractService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static banking.persistence.SqlQueries.CREATE_TABLE_SQL;

public class DbService extends AbstractService {

    private final String url;
    private Connection connection;

    public DbService(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
    }

    public void withConnection(Consumer<Connection> action) {
        action.accept(Objects.requireNonNull(connection));
    }

    public <R> R withConnection(Function<Connection, R> action) {
        return action.apply(Objects.requireNonNull(connection));
    }

    @Override
    protected void doStart() {
        openConnection();
        withConnection(connection -> {
            try (Statement stmt = this.connection.createStatement()) {
                stmt.executeUpdate(CREATE_TABLE_SQL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        notifyStarted();
    }

    private void openConnection() {
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doStop() {
        closeConnection();
        notifyStopped();
    }

    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
