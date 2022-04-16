package gateway;

import model.Plant;
import model.PlantedPlant;
import model.Plot;
import model.User;
import model.UserPlantRequest;
import model.UserRole;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public synchronized static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Plant.class)
                    .addAnnotatedClass(Plot.class)
                    .addAnnotatedClass(UserRole.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(PlantedPlant.class)
                    .addAnnotatedClass(UserPlantRequest.class)
                    .buildSessionFactory();
        }
        return sessionFactory;
    }

    public synchronized static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}