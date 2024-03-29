package com.example.warehouses.DTO;

import com.example.warehouses.model.warehouse.Address;
import com.example.warehouses.model.warehouse.Warehouse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WarehouseDTO {

    private final Long id;
    private final Address address;
    private final String warehouseName;
    private final Double squareFeet;
    private final Double temperature;
    private final Double humidityPercent;
    private final String stockedGoodsType;
    private final String warehouseCategory;
    private final boolean rented;

    public WarehouseDTO(Warehouse warehouse) {
        this.id = warehouse.getId();
        this.address = warehouse.getAddress();
        this.warehouseName = warehouse.getName();
        this.squareFeet = warehouse.getSquareFeet();
        this.temperature = warehouse.getTemperature();
        this.humidityPercent = warehouse.getHumidityPercent();
        this.stockedGoodsType = warehouse.getInventory();
        this.warehouseCategory = warehouse.getCategory();
        this.rented = warehouse.isRented();
    }
}
