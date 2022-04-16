package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class User implements GenericModel, Serializable {
    private static final long serialVersionUID = 8251945427702838734L;

    private Long id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private UserRole userRole;

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}