package com.example.warehouses.controller.user;

import com.example.warehouses.DTO.AgentDTO;
import com.example.warehouses.DTO.WarehouseDTO;
import com.example.warehouses.configurations.Enum.WarehouseCategory;
import com.example.warehouses.model.AgentRatings;
import com.example.warehouses.model.user.Agent;
import com.example.warehouses.model.warehouse.Address;
import com.example.warehouses.model.warehouse.Warehouse;
import com.example.warehouses.services.OwnerService;
import com.example.warehouses.util.OwnerUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Validated
@RequestMapping(path = "/api/main/owners")
public class OwnerController {
    private ExecutorService executorService;
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService clientFuncService) {
        this.ownerService = clientFuncService;
    }

    @PostConstruct
    public void Init() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void closeCustom() {
        executorService.shutdown();
    }

    // Users

    @GetMapping("/warehouses/{owner_id}")
    public ResponseEntity<List<WarehouseDTO>> getAllWarehousesOwnedBy(@PathVariable @Min(value = 0, message = "Invalid id, input number is" +
            "lower than 1 or not a hall number") long owner_id) {
        List<WarehouseDTO> warehouseList = new ArrayList<>();
        List<Warehouse> warehousesOpt = ownerService.getWarehouseByOwnerId(owner_id);
        if (!warehousesOpt.isEmpty()) {
            for (Warehouse warehouse : warehousesOpt
            ) {
                warehouseList.add((new WarehouseDTO(warehouse)));
            }
        }

        return ResponseEntity.ok(warehouseList);
    }


    // TODO look at implmentantation of this method and all methods underneath and possibly refactor if it doesn't make sense.
    @GetMapping("/agents/remove/owners/{ownerId}/warehouses/{warehouseId}/agents")
    public ResponseEntity<Set<AgentDTO>> RemoveAgentsFromWarehouse(@PathVariable @Min(value = 1) Long ownerId,
                                                   @PathVariable @Min(value = 1) Long warehouseId,
                                                   @RequestParam @NotEmpty List<Long> agents) {
        Set<AgentDTO> agentsLeftDTO = ownerService.RemoveAgentsFromWarehouse(ownerId, agents, warehouseId);

        return ResponseEntity.ok(agentsLeftDTO);
    }

    // TODO look at at service method
    @GetMapping("/market/warehouse/owner/{ownerId}/warehouse/{warehouseId}/agentsId")
    public ResponseEntity<Set<Agent>> setAgentsToWarehouse(@PathVariable @Min(value = 1) Long ownerId,
                                           @PathVariable @Min(value = 1) Long warehouseId,
                                           @RequestParam @NotEmpty List<Long> agentsId) {

        return ResponseEntity.ok(ownerService.setAgentsToWarehouse(ownerId, agentsId, warehouseId));
    }

    @GetMapping("/agents")
    public ResponseEntity<Set<AgentDTO>> getAllAgents() {
        return ResponseEntity.ok(ownerService.getAllAgents());
    }

    @GetMapping("/agents/{id}")
    public ResponseEntity<AgentDTO> getAgent(@PathVariable @Min(value = 1) Long id) {
        return ResponseEntity.ok(ownerService.getAgent(id));
    }

    @PostMapping("/create/warehouse")
    public ResponseEntity<WarehouseDTO> createWarehouse(
            @RequestParam("ownerId") String email,
            @Valid @ModelAttribute Address address,
            @Valid @ModelAttribute Warehouse warehouse) {
        WarehouseCategory category = OwnerUtil.warehouseCategory(warehouse.getCategory());
        return ResponseEntity.ok(ownerService.createWarehouse(
                email,
                address.getCounty(),
                address.getTown(),
                address.getStreetName(),
                warehouse.getName(),
                warehouse.getSquareFeet(),
                warehouse.getTemperature(),
                warehouse.getHumidityPercent(),
                warehouse.getInventory(),
                category));
        // possibly return a view with a list of agents to pick from.
    }


    // TODO
    // edit/warehouses func() { return edit page view from which to send new post request
    //  to make individual agent or list of agents pick to represent it - > edit/warehouse/agents}

    // TODO
    // edit/warehouse/agents func() { put - add/remove agents from db }


    @GetMapping("rented-warehouses/{status}")
    public ResponseEntity<List<WarehouseDTO>> getWarehouseByStatus(@PathVariable(required = false) String status) {
        List<WarehouseDTO> warehouseDTOlist = new ArrayList<>();
        for (Warehouse warehouse : ownerService.getAllWarehouses(status)
        ) {
            warehouseDTOlist.add(new WarehouseDTO(warehouse));
        }

        return ResponseEntity.ok(warehouseDTOlist);
    }

    @GetMapping("/market/{ownerId}")
    public ResponseEntity<List<Warehouse>> fetchWarehouses(@PathVariable @Min(value = 1) Long ownerId) { //
        List<Warehouse> allOwnerWarehouses = ownerService.fetchWarehouses(ownerId);
        System.out.println(allOwnerWarehouses.size());
        ModelAndView modelAndView = new ModelAndView("/main/testFetch");
        modelAndView.addObject("dataList", allOwnerWarehouses);
        return ResponseEntity.ok(allOwnerWarehouses);
    }

    @PostMapping("rate-agent")
    public ResponseEntity<List<AgentRatings>> rateAgent(@RequestParam @Min(value = 1) Long ownerId,
                                        @RequestParam @Min(value = 1) Long agentId,
                                        @RequestParam @Min(value = 1) @Max(value = 5) int stars) {
        return ResponseEntity.ok(ownerService.rateAgent(ownerId, agentId, stars));
    }

}
