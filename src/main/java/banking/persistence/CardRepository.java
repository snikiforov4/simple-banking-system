package banking.persistence;

import banking.Card;
import com.google.common.base.Preconditions;

import java.sql.*;
import java.util.Optional;

import static banking.persistence.SqlQueries.*;

public class CardRepository implements ICardRepository {

    private final DbService dbService;

    public CardRepository(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        return dbService.withConnection(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_CARD_BY_NUMBER)) {
                stmt.setString(1, cardNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasNext = rs.next();
                    if (!hasNext) {
                        return Optional.empty();
                    }
                    Card card = new Card();
                    card.setId(rs.getLong(1));
                    card.setNumber(rs.getString(2));
                    card.setPin(rs.getString(3));
                    card.setBalance(rs.getInt(4));
                    return Optional.of(card);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Card save(Card card) {
        dbService.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_CARD, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, card.getNumber());
                statement.setString(2, card.getPin());
                int affectedRows = statement.executeUpdate();
                Preconditions.checkState(affectedRows == 1, "Insertion failed");
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        card.setId(generatedKeys.getLong(1));
                    } else {
                        throw new RuntimeException("No ID obtained");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return card;
    }

    @Override
    public void deleteByCardNumber(String cardNumber) {
        dbService.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CARD_BY_NUMBER)) {
                statement.setString(1, cardNumber);
                int affectedRows = statement.executeUpdate();
                Preconditions.checkState(affectedRows == 1, "Deletion failed");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public long updateBalance(String cardNumber, long income) {
        return dbService.withConnection(connection -> {
            try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_CARD_BALANCE_BY_CARD_NUMBER)) {
                final long currentBalance = getCurrentBalance(connection, cardNumber);
                final long newBalance = Math.addExact(currentBalance, income);
                executeBalanceUpdate(cardNumber, updateStatement, newBalance);
                return newBalance;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void executeBalanceUpdate(String cardNumber, PreparedStatement updateStatement, long newBalance) throws SQLException {
        updateStatement.setLong(1, newBalance);
        updateStatement.setString(2, cardNumber);
        int affectedRows = updateStatement.executeUpdate();
        Preconditions.checkState(affectedRows == 1,
                "Balance update failed! Affected rows: %s", affectedRows);
    }

    @Override
    public long transfer(final String fromCardNumber,
                         final String toCardNumber,
                         final long delta) {
        return dbService.withConnection(connection -> {
            try (PreparedStatement fromUpdateStatement = connection.prepareStatement(UPDATE_CARD_BALANCE_BY_CARD_NUMBER);
                 PreparedStatement toUpdateStatement = connection.prepareStatement(UPDATE_CARD_BALANCE_BY_CARD_NUMBER)) {
                connection.setAutoCommit(false);
                final long fromBalance = getCurrentBalance(connection, fromCardNumber);
                final long toBalance = getCurrentBalance(connection, toCardNumber);
                final long newFromBalance = Math.subtractExact(fromBalance, delta);
                final long newToBalance = Math.addExact(toBalance, delta);
                executeBalanceUpdate(fromCardNumber, fromUpdateStatement, newFromBalance);
                executeBalanceUpdate(toCardNumber, toUpdateStatement, newToBalance);
                connection.commit();
                return newFromBalance;
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                }
                throw new RuntimeException(e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        });
    }

    private long getCurrentBalance(Connection connection, String cardNumber) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_CARD_BALANCE_BY_CARD_NUMBER)) {
            statement.setString(1, cardNumber);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Card not found!");
                }
                return rs.getLong(1);
            }
        }
    }
}
