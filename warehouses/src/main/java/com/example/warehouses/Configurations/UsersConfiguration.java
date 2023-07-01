package com.example.warehouses.Configurations;

import com.example.warehouses.Configurations.Enum.ActivityType;
import com.example.warehouses.Configurations.Enum.WarehouseCategory;
import com.example.warehouses.Interfaces.Administrator;
import com.example.warehouses.Model.AgentRatings;
import com.example.warehouses.Model.AgentRatingsPK;
import com.example.warehouses.Model.Notification;
import com.example.warehouses.Model.User.Agent;
import com.example.warehouses.Model.User.Client;
import com.example.warehouses.Model.User.MasterAdmin;
import com.example.warehouses.Model.User.Owner;
import com.example.warehouses.Model.warehouse.*;
import com.example.warehouses.Repository.*;
import com.example.warehouses.Services.GlobalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class UsersConfiguration {

    @Bean
    CommandLineRunner commandLineRunner(AdminRepository repository,
                                        ClientRepository clientRepository,
                                        RatingsRepository ratingsRepository,
                                        RentalFormRepository rentalFormRepository,
                                        WarehouseRepository warehouseRepository,
                                        AddressRepository addressRepository,
                                        WarehouseAssignedToAgentRepository marketRepository,
                                        NotificationRepository notificationRepository,
                                        GlobalService globalService){
        return args -> {
            Administrator admin1 = new MasterAdmin();
            ((MasterAdmin) admin1).init( "admin1@gmail.com",
                    "Monkeyfingers",
                    "Ivan",
                    "Dimitrov");

            Administrator admin2 = new MasterAdmin();
            ((MasterAdmin) admin2).init( "admin2@gmail.com",
                    "LolipopsYeee",
                    "Jack",
                    "Daniels");

           Client client = new Owner();
            ((Owner) client).init(
                    "client1@gmail.com",
                    "hello",
                    "Black",
                    "Swan"
            );

            Client client1 = new Owner();
            ((Owner) client1).init(
                    "owner2@gmail.com",
                    "hello",
                    "White",
                    "Truffle"
            );


            Agent agent1 = new Agent();
            agent1.init(  "agent1@gmail.com",
                    "hello",
                    "Orange",
                    "Juice");
            Agent agent2 = new Agent();
            agent2.init(  "agent2@gmail.com",
                    "hello",
                    "Yellow",
                    "Lamborghini");


            repository.saveAll(
                    List.of(admin1,admin2)

            );

            List<Client> clientList = new ArrayList<>();

            for (Client item: List.of(client,client1,agent1,agent2)
                 ) {
                if(globalService.isEmailInDatabase(item.getEmail())==false){
                    clientList.add(item);
                }
            }
            clientRepository.saveAll(
                   clientList
            );

            Address address1 = new Address();
            address1.init("Varna","Varna","Dobrovnik 13, vh A");
            Address address2 = new Address();
            address2.init("Targovishte","Opaka","Tsar osvoboditel 2");


            addressRepository.saveAll(
                    List.of(address1,address2)
            );

            Warehouse warehouse1 = new Warehouse();
            warehouse1.init((Owner)client1,address1,"EcontVarnaMain", "1000","22","25","retail",
                    WarehouseCategory.INDUSTRIAL);

            Warehouse warehouse2 = new Warehouse();
            warehouse2.init((Owner)client1,address2,"SkladZaDrehi", "1000","17","33","clothes",
                    WarehouseCategory.GARAGE);


            warehouseRepository.save(warehouse1);
            warehouseRepository.save(warehouse2);

            AgentRatingsPK pk1 = new AgentRatingsPK(client.getId(), agent1.getId());
            AgentRatingsPK pk11 = new AgentRatingsPK(client.getId(),agent2.getId());
            AgentRatingsPK pk2 = new AgentRatingsPK(client1.getId(),agent1.getId());
            AgentRatingsPK pk22 = new AgentRatingsPK(client1.getId(),agent2.getId());
            AgentRatings rating1 = new AgentRatings(pk1, 5);
            AgentRatings rating2 = new AgentRatings(pk11, 2);
            AgentRatings rating3 = new AgentRatings(pk2, 4);
            AgentRatings rating4 = new AgentRatings(pk22, 1);


            ratingsRepository.saveAll(
                    List.of(rating1,rating2,rating3
                    ,rating4)
            );
            Optional<List<AgentRatings>> ratingsBasedOnOwnerID = ratingsRepository.findByOwnerId(1L);
            Optional<List<AgentRatings>> ratingsBasedOnAgentID = ratingsRepository.findAllByAgentId(3L);
//
            for (AgentRatings item: ratingsBasedOnOwnerID.get()
              ) {
                System.out.println("Owner ratings with id's of 1L value " + item);
            }

            for (AgentRatings item: ratingsBasedOnAgentID.get()
            ) {
                System.out.println("Agent ratings with id's of 3L value " + item);
            }

            LocalDate start = LocalDate.of(2021,4,24);
            LocalDate end = LocalDate.of(2023,6,26);
            LocalDate start1 = LocalDate.of(2020,1,24);
            LocalDate end1 = LocalDate.of(2024,1,26);

            warehouseRepository.save(warehouse1);
            Optional<List<Warehouse>> warehouses = warehouseRepository.findByRentStatus(true);
            for (Warehouse item : warehouses.get()
                 ) {
                System.out.print(item);
            }

            WarehouseAsignedToAgentPK rentPK1 = new WarehouseAsignedToAgentPK(agent1.getId(),warehouse1.getId());
            WarehouseAsignedToAgentPK rentPK2 = new WarehouseAsignedToAgentPK(agent2.getId(),warehouse1.getId());
            WarehouseAsignedToAgentPK rentPK22 = new WarehouseAsignedToAgentPK(agent2.getId(),warehouse2.getId());
            WarehouseAssignedToAgent rent1 = new WarehouseAssignedToAgent(rentPK1);
            WarehouseAssignedToAgent rent2 = new WarehouseAssignedToAgent(rentPK2);
            WarehouseAssignedToAgent rent22 = new WarehouseAssignedToAgent(rentPK22);
            marketRepository.saveAll(
                    List.of(rent1,rent2,rent22)
            );


            if(marketRepository.findById(new WarehouseAsignedToAgentPK(4L,2L)).isPresent()){
                RentalForm rentalForm1 = new RentalForm(agent2,agent1,warehouse1,start,end,5000,0.02);
                rentalFormRepository.save(rentalForm1);
            }


            Notification notificationToAgent = new Notification((Owner)client, agent1, warehouse1, ActivityType.ASIGNED_TO_AGENT);
            Notification notificationToOwner = new Notification((Owner)client, agent1, warehouse1, ActivityType.RENTED_OUT);
            Notification notificationContractExpire = new Notification((Owner)client, agent1, warehouse1, ActivityType.CONTRACT_EXPIRATION);

            notificationRepository.saveAll(
                    List.of(notificationToAgent,notificationToOwner,notificationContractExpire)
            );


            RentalForm rentalForm1 = new RentalForm(agent2,agent1,warehouse1,start,end,500.50,0.2);
            RentalForm rentalForm2 = new RentalForm(agent2,client1,warehouse1,start1,end1,444,0.5);
            rentalFormRepository.save(rentalForm1);
            rentalFormRepository.save(rentalForm2);


        };
    }
}
