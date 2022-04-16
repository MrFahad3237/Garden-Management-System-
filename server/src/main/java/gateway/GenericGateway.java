package gateway;

import model.GenericModel;
import org.hibernate.Session;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class GenericGateway<T extends GenericModel> {
    protected final Class<T> tClass;

    @SuppressWarnings("unchecked")
    protected GenericGateway() {
        this.tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T save(T obj) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        session.save(obj);

        session.getTransaction().commit();
        session.close();

        return obj;
    }

    public void update(Long id, T obj) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        obj.setId(id);
        session.merge(obj);//update detached instance

        session.getTransaction().commit();
        session.close();
    }

    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        //this version also deletes children
        T t = session.get(tClass, id);
        session.delete(t);

        session.getTransaction().commit();
        session.close();
    }

    public T findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        T obj = session.get(tClass, id);

        session.getTransaction().commit();
        session.close();

        return obj;
    }

    public List<T> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List<T> objs = session.createQuery("FROM " + tClass.getSimpleName()).getResultList();

        session.getTransaction().commit();
        session.close();

        return objs;
    }
}
