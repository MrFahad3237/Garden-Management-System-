package model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class UserRole implements GenericModel, Serializable {

    private static final long serialVersionUID = 7177210527192464006L;
    @Setter
    private Long id;

    @NonNull
    @Column
    private String name;

    @NonNull
    private Set<User> users;

    @Override
    public String toString() {
        return name;
    }
}