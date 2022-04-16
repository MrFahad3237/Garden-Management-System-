package View;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import model.Garden;
import model.Plant;
import model.User;
import model.UserRole;

import java.util.Set;

@RequiredArgsConstructor
public class CrudModel {

    @Getter
    @Setter
    @NonNull
    private Garden garden;

    @Getter
    @Setter
    @NonNull
    private Set<User> regularUsers;

    @Getter
    @Setter
    @NonNull
    private Set<Plant> plants;

    @Getter
    @Setter
    private UserRole regularRole;

    public void addPlant(Plant plant) {
        plants.remove(plant);
        plants.add(plant);
    }

    public void addUser(User user) {
        regularUsers.remove(user);
        regularUsers.add(user);
    }

    public void deletePlant(Long id) {
        plants.removeIf(x -> x.getId().equals(id));
    }

    public void deleteUser(Long id) {
        regularUsers.removeIf(x -> x.getId().equals(id));
    }
}
