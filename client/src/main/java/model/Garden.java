package model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class Garden implements Serializable {

    private static final long serialVersionUID = 8323041943737478982L;
    private int width = 14;
    private int height = 14;

    @NonNull
    private Set<Plot> plots;
    @NonNull
    private Set<PlantedPlant> plantedPlants;
}
