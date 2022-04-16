package Controller.util;

import model.Garden;
import model.PlantedPlant;
import model.Plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;

public class TxtMaker implements FileMaker {

    @Override
    public void generateFileWithGarden(String oldPath, List<PlantedPlant> plantedPlants, List<Plot> plots, Garden garden) {
        String path = oldPath;

        //check if contains .txt extension
        if (path.length() > 3 && !path.substring(path.length() - 4).equals(".txt")) {
            path += ".txt";
        }

        try (PrintWriter writer = new PrintWriter(new File(path))) {
            writer.println("Garden Representation");
            writer.println();

            writer.println("Garden width=" + garden.getWidth() + " height=" + garden.getHeight());
            writer.println();
            writer.println("Plots:");

            for (Plot plot : plots) {
                writer.println(plot.toString());
            }

            writer.println();
            writer.println("Plants which are planted in the garden:");

            for (PlantedPlant plantedPlant : plantedPlants) {
                writer.println(plantedPlant.toString());
            }
        } catch (FileNotFoundException e) {
            if(LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
    }
}
