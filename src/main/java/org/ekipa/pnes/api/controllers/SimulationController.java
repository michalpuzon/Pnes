package org.ekipa.pnes.api.controllers;

import org.ekipa.pnes.api.configs.security.Controller;
import org.ekipa.pnes.api.configs.security.SecuredMapping;
import org.ekipa.pnes.models.netModels.NetModel;
import org.ekipa.pnes.models.netModels.PTNetModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/simulation")
public class SimulationController extends Controller {

    @SecuredMapping(path = "/pt-net", method = RequestMethod.POST)
    public List<List<NetModel>> ptNetModelSimulation(@RequestBody PTNetModel startingModel, @RequestParam(defaultValue = "1") int cycles) throws Exception {
        return NetModel.simulate(startingModel, cycles);
    }
}
