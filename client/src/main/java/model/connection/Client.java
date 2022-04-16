package model.connection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Client {
    @NonNull

    private String userName;
    @NonNull
    private String password;

    @NonNull
    private String role;

}
