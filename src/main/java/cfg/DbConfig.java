package cfg;
/** ValueObject */
public class DbConfig {

    public DbConfig(String name, String password, String user, int port, String host) {
        this.name = name;
        this.password = password;
        this.user = user;
        this.port = port;
        this.host = host;
    }

    private final String name;
    private final String password;
    private final String user;
    private final int port;
    private final String host;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
