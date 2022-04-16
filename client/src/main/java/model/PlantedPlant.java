package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class PlantedPlant implements GenericModel, Serializable {

    private static final long serialVersionUID = -628931848321202942L;

    private Long id;

    @NonNull
    private int x;//relative to the garden

    @NonNull
    private int y;//relative to the garden

    @NonNull
    private Plant plant;//what type

    @Override
    public String toString() {
        String repr = "x=" + x +
                ", y=" + y;
        if (plant.getType() != null) {
            repr += ", plant type=" + plant.getType();
        }
        return repr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        PlantedPlant that = (PlantedPlant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
