package Controller.util;

import model.Garden;
import model.PlantedPlant;
import model.Plot;

import java.util.List;
import java.util.logging.Logger;

public interface FileMaker {
    Logger LOGGER = Logger.getLogger(FileMaker.class.getName());

    void generateFileWithGarden(String path, List<PlantedPlant> plantedPlants, List<Plot> plots, Garden garden);

}
