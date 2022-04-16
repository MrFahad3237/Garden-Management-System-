package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Plant implements GenericModel, Serializable {

    private static final long serialVersionUID = -3330225826069084076L;
    private Long id;

    @NonNull
    private String type;

    @NonNull
    private Integer averageLife;//life in seconds

    @NonNull
    private Integer waterRequirements;//amount of water needed every minute

    @NonNull
    private Integer plotSize;//plants are placed in a square area, 'plotSize' is the length=width of the square

    @NonNull
    private Integer stockSize;

    private List<PlantedPlant> plantedPlants;

    @Override
    public String toString() {
        return "type=" + type + '\'' +
                ", averageLife=" + averageLife +
                ", waterRequirements=" + waterRequirements +
                ", plotSize=" + plotSize +
                ", stockSize= " + stockSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Plant plant = (Plant) o;
        return Objects.equals(id, plant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
