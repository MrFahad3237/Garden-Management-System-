package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "plots")
public class Plot implements GenericModel, Serializable {

    private static final long serialVersionUID = 1792464730449276650L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column
    private int width;

    @NonNull
    @Column
    private int height;

    @NonNull
    @Column
    private int upperX;//relative to (0,0)

    @NonNull
    @Column
    private int upperY;

    @Override
    public String toString() {
        return "width=" + width +
                ", height=" + height +
                ", upperX=" + upperX +
                ", upperY=" + upperY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Plot plot = (Plot) o;
        return Objects.equals(id, plot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
