package bll.service;

import bll.exceptions.DuplicateEntityException;
import bll.validators.Validator;
import gateway.GenericGateway;
import model.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantService extends GenericService<Plant> {
    public PlantService(GenericGateway<Plant> genericGateway) {
        super(genericGateway);
    }

    @Override
    public List<Validator<Plant>> getValidators() {
        return new ArrayList<>();
    }

    @Override
    protected boolean isDuplicated(Long id, Plant object) {
        List<Plant> objects = findAll();
        for (Plant obj : objects) {
            if (id == null || !id.equals(obj.getId())) {
                if (obj.getType().equals(object.getType())) {
                    throw new DuplicateEntityException("Duplicate Type!");
                }
            }
        }
        return false;
    }
}
