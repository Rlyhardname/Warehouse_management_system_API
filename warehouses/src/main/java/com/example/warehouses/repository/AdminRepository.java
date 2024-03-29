package com.example.warehouses.repository;

import com.example.warehouses.interfaces.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Administrator,Long> {

   // @Query("Select s FROM Administrator s WHERE s.email =?1")
    Optional<Administrator> findByEmail(String email);

}
