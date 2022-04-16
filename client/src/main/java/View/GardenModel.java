package View;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Garden;
import model.Plant;
import model.PlantedPlant;


import java.util.List;

@NoArgsConstructor
@Data
public class GardenModel {
    private Garden garden;
    private List<Plant> plants;
    private Long chosenPlantId;
    private String[][][] gardenRepr;

    public void addPlantedPlant(PlantedPlant plant) {
        garden.getPlantedPlants().add(plant);
    }
    
}
