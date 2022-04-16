package model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@RequiredArgsConstructor
@Getter
public class UserPlantRequest implements GenericModel, Serializable {
    private static final long serialVersionUID = 6559489225861850960L;

    @Setter
    private Long id;

    @NonNull
    private Plant plant;

    @NonNull
    private User user;

    @NonNull
    private Long amount;

    private String status;

    @Override
    public String toString() {
        return "plant=" + plant.getType() +
                ", user=" + user.getUsername() +
                ", amount=" + amount;
    }
}
