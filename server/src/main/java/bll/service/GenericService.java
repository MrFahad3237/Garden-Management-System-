package bll.service;

import bll.validators.GlobalLockHelper;
import bll.validators.Validator;
import gateway.GenericGateway;
import model.GenericModel;

import java.util.List;

public abstract class GenericService<T extends GenericModel> {
    protected GenericGateway<T> genericGateway;

    public GenericService(GenericGateway<T> genericGateway) {
        this.genericGateway = genericGateway;
    }

    public T findById(Long id) {
        return genericGateway.findById(id);
    }

    public List<T> findAll() {
        return genericGateway.findAll();
    }

    public T save(T object) {
        GlobalLockHelper.LOCK.lock();

        try {
            if (isDuplicated(null, object)) {
                return null;//the method throws exception but this is done in this way to be able to add extensions later
            }

            if (!validateAll(object)) {
                return null;
            }

            return genericGateway.save(object);
        } finally {
            GlobalLockHelper.LOCK.unlock();
        }
    }

    public void update(Long id, T object) {
        GlobalLockHelper.LOCK.lock();

        try {
            if (isDuplicated(id, object)) {
                return;//the method throws exception but this is done in this way to be able to add extensions later
            }

            if (!validateAll(object)) {
                return;
            }

            genericGateway.update(id, object);
        } finally {
            GlobalLockHelper.LOCK.unlock();
        }
    }

    public void delete(Long id) {
        GlobalLockHelper.LOCK.lock();
        
        try {
            genericGateway.delete(id);
        } finally {
            GlobalLockHelper.LOCK.unlock();
        }
    }

    public abstract List<Validator<T>> getValidators();

    public boolean validateAll(T p) {
        for (Validator<T> validator : getValidators()) {
            if (!validator.validate(p)) {
                return false;
            }
        }
        return true;
    }

    protected abstract boolean isDuplicated(Long id, T object);
}
