package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "plant")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Plant implements GenericModel, Serializable {
    private static final long serialVersionUID = -3330225826069084076L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plant_id")
    private Long id;

    @OneToMany(mappedBy = "plant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserPlantRequest> requests;

    @OneToMany(mappedBy = "plant", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PlantedPlant> plantedPlants;

    @NonNull
    @Column
    private String type;

    @NonNull
    @Column
    private Integer averageLife;//life in seconds

    @NonNull
    @Column
    private Integer waterRequirements;//amount of water needed every minute

    @NonNull
    @Column
    private Integer plotSize;//plants are placed in a square area, 'plotSize' is the length=width of the square

    @NonNull
    @Column
    private Integer stockSize;

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
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
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