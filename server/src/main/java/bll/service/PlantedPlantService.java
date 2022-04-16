package bll.service;

import bll.exceptions.OutOfStockException;
import bll.validators.EnoughSpaceValidator;
import bll.validators.Validator;
import gateway.GenericGateway;
import model.Plant;
import model.PlantedPlant;

import java.util.ArrayList;
import java.util.List;

public class PlantedPlantService extends GenericService<PlantedPlant> {
    private PlantService plantService;

    public PlantedPlantService(GenericGateway<PlantedPlant> genericGateway, PlantService plantService) {
        super(genericGateway);
        this.plantService = plantService;
    }

    @Override
    public List<Validator<PlantedPlant>> getValidators() {
        List<Validator<PlantedPlant>> validators = new ArrayList<>();
        validators.add(new EnoughSpaceValidator(genericGateway.findAll()));
        return validators;
    }

    @Override
    protected boolean isDuplicated(Long id, PlantedPlant object) {
        return false;
    }

    @Override
    public PlantedPlant save(PlantedPlant object) {
        //decrement plant stock
        Plant plant = object.getPlant();

        if (plant.getStockSize() < 1) {
            throw new OutOfStockException("Not enough Stock");
        }

        plant.setStockSize(plant.getStockSize() - 1);

        plantService.update(plant.getId(), plant);

        return super.save(object);
    }
}
