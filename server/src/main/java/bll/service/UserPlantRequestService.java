package bll.service;

import bll.validators.Validator;
import gateway.GenericGateway;
import model.Plant;
import model.UserPlantRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserPlantRequestService extends GenericService<UserPlantRequest> {
    private PlantService plantService;

    public UserPlantRequestService(GenericGateway<UserPlantRequest> genericGateway,
                                   PlantService plantService) {
        super(genericGateway);
        this.plantService = plantService;
    }

    public List<UserPlantRequest> findNotAccepted() {
        return findAll().stream().filter(t -> t.getStatus() == null).collect(Collectors.toList());
    }

    public List<UserPlantRequest> findAllAccepted() {
        return findAll().stream().filter(t -> "accepted".equals(t.getStatus())).collect(Collectors.toList());
    }

    public List<UserPlantRequest> findAllDenied() {
        return findAll().stream().filter(t -> "denied".equals(t.getStatus())).collect(Collectors.toList());
    }

    public void acceptRequest(Long id) {
        UserPlantRequest request = findById(id);
        request.setStatus("accepted");
        update(id, request);

        //update stock
        Plant plant = request.getPlant();
        plant.setStockSize((int) (plant.getStockSize() + request.getAmount()));
        plantService.update(plant.getId(), plant);
    }

    public void denyRequest(Long id) {
        UserPlantRequest request = findById(id);
        request.setStatus("denied");
        update(id, request);
    }

    @Override
    public List<Validator<UserPlantRequest>> getValidators() {
        return new ArrayList<>();
    }

    @Override
    protected boolean isDuplicated(Long id, UserPlantRequest object) {
        return false;
    }
}
