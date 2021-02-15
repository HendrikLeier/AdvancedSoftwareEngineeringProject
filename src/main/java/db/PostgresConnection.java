package db;

import cfg.ConfigProvider;
import cfg.IncompleteConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;

public class PostgresConnection {
    private static Connection connection;

    private static final Logger logger = LogManager.getLogger(PostgresConnection.class);

    public static Connection getDbConnection() throws ClassNotFoundException, IncompleteConfigException, SQLException, IOException {
        if (connection == null) {
            connection = establishConnection();
        }
        return connection;
    }

    private static Connection establishConnection() throws IOException, IncompleteConfigException, ClassNotFoundException, SQLException {
        ConfigProvider configProvider = ConfigProvider.getInstance();
        Class.forName("org.postgresql.Driver");

        Connection c = DriverManager.getConnection("jdbc:postgresql://"
                    +configProvider.getDbConfig().getHost()+
                    ":"+configProvider.getDbConfig().getPort()+
                    "/"+configProvider.getDbConfig().getName(),
                    configProvider.getDbConfig().getUser(),
                    configProvider.getDbConfig().getPassword());

        logger.info("Postgres-DB connection established!");

        return c;
    }
}
