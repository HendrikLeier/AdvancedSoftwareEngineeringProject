import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

// Singleton
public class PersistenceSingleton {

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("ASEProjPU");
        }

        return entityManagerFactory;
    }

}
