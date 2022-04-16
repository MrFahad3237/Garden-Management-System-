package bll.service;

import bll.exceptions.DuplicateEntityException;
import bll.validators.Validator;
import gateway.GenericGateway;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService extends GenericService<User> {
    public UserService(GenericGateway<User> genericGateway) {
        super(genericGateway);
    }

    public User findByUserName(String userName) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findRegularUsers() {
        List<User> users = genericGateway.findAll();
        return users.stream().filter(u -> u.getUserRole().getName().equals("user")).collect(Collectors.toList());
    }

    @Override
    public List<Validator<User>> getValidators() {
        return new ArrayList<>();
    }

    @Override
    protected boolean isDuplicated(Long id, User object) {
        List<User> users = findAll();

        for (User user : users) {

            if (id == null || !id.equals(user.getId())) {
                if (user.getUsername().equals(object.getUsername())) {
                    throw new DuplicateEntityException("Duplicate username!");
                }
            }
        }
        return false;
    }
}
