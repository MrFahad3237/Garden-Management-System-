package bll.validators;

import bll.exceptions.RedundantDataException;
import model.PlantedPlant;

import java.util.List;

public class EnoughSpaceValidator implements Validator<PlantedPlant> {

    private List<PlantedPlant> plantedPlants;

    public EnoughSpaceValidator(List<PlantedPlant> plantedPlants) {
        this.plantedPlants = plantedPlants;
    }

    @Override
    public boolean validate(PlantedPlant p1) {
        //check if there isn't another plant planted in the same space
        for (PlantedPlant p2 : plantedPlants) {
            int size1 = p1.getPlant().getPlotSize();
            int size2 = p2.getPlant().getPlotSize();

            if (p1.getX() < (p2.getX() + size2) && (p1.getX() + size1) > p2.getX() &&
                    p1.getY() < (p2.getY() + size2) && (p1.getY() + size1) > p2.getY()) {
                //collision
                throw new RedundantDataException("Not enough space! Updating Data...!");
            }
        }
        return true;
    }
}
