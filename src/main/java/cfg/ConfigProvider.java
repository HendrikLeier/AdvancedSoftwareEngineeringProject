package cfg;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

// Value Object because of immutable Singleton...
// All included values have been checked for completeness
public class ConfigProvider {

    private ConfigProvider(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    private final DbConfig dbConfig;

    private static final String iniPath = "config/appcfg.ini";

    /** Mandatory preferences */
    private static final String[] requiredDbValues = {"user", "password", "name", "port", "host"};

    private static Preferences preferences;
    private static ConfigProvider instance;

    private static void initIni() throws IOException {
        Ini iniFile = new Ini(new File(iniPath));
        preferences = new IniPreferences(iniFile);
    }

    public static ConfigProvider getInstance() throws IOException, IncompleteConfigException {
        if (instance == null) {
            initIni();

            Preferences dbNode = preferences.node("db");

            instance = new ConfigProvider(ConfigProvider.getDbConfig(dbNode));
        }

        return instance;
    }

    private static DbConfig getDbConfig(Preferences dbNode) throws IncompleteConfigException {
        for (String requiredValue : requiredDbValues) {
            String val = dbNode.get(requiredValue, null);
            if (val == null) {
                throw new IncompleteConfigException("Missing parameter "+requiredValue);
            }
        }

        return new DbConfig(
                dbNode.get("name", null),
                dbNode.get("user", null),
                dbNode.get("password", null),
                Integer.parseInt(dbNode.get("port", null)),
                dbNode.get("host", null)
        );
    }

    public DbConfig getDbConfig() {
        return dbConfig;
    }
}
