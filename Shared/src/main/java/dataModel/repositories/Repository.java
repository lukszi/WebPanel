package dataModel.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Created by Lukas Szimtenings on 29.01.2017.
 * Base repository abstracting session management
 */
public abstract class Repository
{
    protected SessionFactory sessionFactory;
    protected Session session;
    protected Transaction tx;
    
    /**
     * Initializes a new Session and a belonging transaction
     */
    protected void initSession()
    {
        if(sessionFactory != null && sessionFactory.isOpen())
            return;
        if(session!=null && session.isOpen())
            return;
        if(tx != null && tx.isActive())
            return;
        sessionFactory = new Configuration().
                configure()
                .buildSessionFactory();
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
    }
    
    /**
     * Closes a Session, also closing it's Transaction and SessionFactory
     */
    protected void endSession()
    {
        if(tx == null || session == null || sessionFactory == null)
            return;
        tx.commit();
        session.close();
        sessionFactory.close();
    }
}
