import cfg.IncompleteConfigException;
import db.PostgresConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ClassNotFoundException, IncompleteConfigException, SQLException, IOException {
        logger.info("App starting!");
        Connection connection = PostgresConnection.getDbConnection();

        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM TEST;");

        while (resultSet.next()) {
            int id = resultSet.getInt("prim_id");
            String name = resultSet.getString("name");

            logger.info("ID = "+id+", name = " + name);
        }
    }

}
