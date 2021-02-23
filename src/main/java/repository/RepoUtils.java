package repository;

import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class RepoUtils {

    public interface EntityTransactionCall<T> {

        void apply(EntityManager em, T object);

    }

    public static <E> boolean transactionEncapsulatedStatement(EntityManager em, E object, EntityTransactionCall<E> call, Logger logger) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            call.apply(em, object);
            tx.commit();
            return true;
        } catch (Exception e) {
            logger.error("Cannot commit:\n" + e.getLocalizedMessage());
            tx.rollback();
            return false;
        }
    }

}
