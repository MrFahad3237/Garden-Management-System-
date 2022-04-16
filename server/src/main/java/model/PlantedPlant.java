package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "planted_plants")
public class PlantedPlant implements GenericModel, Serializable {

    private static final long serialVersionUID = -628931848321202942L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column
    private int x;//relative to the garden

    @NonNull
    @Column
    private int y;//relative to the garden

    @NonNull
    @ManyToOne
    @JoinColumn(name = "plant_id")
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
        if (this == o){
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
