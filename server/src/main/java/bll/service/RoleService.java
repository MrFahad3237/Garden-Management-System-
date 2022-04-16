package bll.service;

import bll.validators.Validator;
import gateway.GenericGateway;
import model.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoleService extends GenericService<UserRole> {
    public RoleService(GenericGateway<UserRole> genericGateway) {
        super(genericGateway);
    }

    public UserRole findByName(String name) {
        List<UserRole> userRoles = genericGateway.findAll();
        return userRoles.stream().filter(u -> u.getName().equals("user")).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<Validator<UserRole>> getValidators() {
        return new ArrayList<>();
    }

    @Override
    protected boolean isDuplicated(Long id, UserRole object) {
        return false;
    }
}
