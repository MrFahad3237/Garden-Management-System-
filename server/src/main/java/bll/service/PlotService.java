package bll.service;

import bll.validators.Validator;
import gateway.GenericGateway;
import model.Plot;

import java.util.ArrayList;
import java.util.List;

public class PlotService extends GenericService<Plot> {
    public PlotService(GenericGateway<Plot> genericGateway) {
        super(genericGateway);
    }

    @Override
    public List<Validator<Plot>> getValidators() {
        return new ArrayList<>();
    }

    @Override
    protected boolean isDuplicated(Long id, Plot object) {
        return false;
    }
}
