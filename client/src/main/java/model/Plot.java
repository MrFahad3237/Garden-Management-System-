package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Plot implements GenericModel, Serializable {

    private static final long serialVersionUID = 1792464730449276650L;
    private Long id;

    @NonNull
    private int width;

    @NonNull
    private int height;

    @NonNull
    private int upperX;//relative to (0,0)

    @NonNull
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
