package com.example.warehouses.model.warehouse;

import com.example.warehouses.model.user.Agent;
import com.example.warehouses.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class RentalForm {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long Id;
    @ManyToOne
    private Agent agent;
    @ManyToOne
    private User user;
    @ManyToOne
    private Warehouse warehouse;
    private LocalDate startDate;
    private LocalDate endDate;
    private double contractFiatWorth;
    private double agentFee;
    private Long contractInDays;

    public RentalForm(Agent agent, User user, Warehouse warehouse, LocalDate startDate, LocalDate endDate, double contractFiatWorth, double agentFee) {
        this.agent = agent;
        this.user = user;
        this.warehouse = warehouse;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractFiatWorth = contractFiatWorth;
        this.agentFee = agentFee;
        warehouse.setRented(true);
        setContractDays();
    }

    public void setContractDays(){
        contractInDays = ChronoUnit.DAYS.between(startDate,endDate);
    }
}