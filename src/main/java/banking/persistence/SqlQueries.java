package banking.persistence;

final class SqlQueries {

    static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS card (
            	id INTEGER NOT NULL PRIMARY KEY,
            	number TEXT NOT NULL,
            	pin TEXT NOT NULL,
            	balance INTEGER DEFAULT 0
            );""";

    static final String INSERT_CARD = "INSERT INTO card(number, pin) VALUES(?, ?)";

    static final String SELECT_CARD_BY_NUMBER = "SELECT id, number, pin, balance FROM card WHERE number = ?";

    static final String SELECT_CARD_BALANCE_BY_CARD_NUMBER = "SELECT balance FROM card WHERE number = ?";

    static final String DELETE_CARD_BY_NUMBER = "DELETE FROM card WHERE number = ?";

    static final String UPDATE_CARD_BALANCE_BY_CARD_NUMBER = "UPDATE card SET balance = ? WHERE number = ?";

    private SqlQueries() {
    }
}
