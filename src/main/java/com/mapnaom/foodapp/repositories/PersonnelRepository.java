package com.mapnaom.foodapp.repositories;

import com.mapnaom.foodapp.models.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long>, JpaSpecificationExecutor<Personnel> {
    boolean existsByPersCode(String name);
    @Query("select (count(p) > 0) from Personnel p where p.persCode = :persCode and p.id <> :id")
    boolean existsPersonnelByPersCodeAndIdIsNot(@Param("persCode") String persCode, @Param("id") Long id);

    boolean existsById(Long id);

    @Query("select (count(p) > 0) from Personnel p where p.username = :username")
    boolean existsPersonnelByUsername(@Param("username") String username);

    @Query("select (count(p) > 0) from Personnel p where p.username = :username and p.id <> :id")
    boolean existsPersonnelByUsernameAndIdIsNot(@Param("username") String username, @Param("id") Long id);

    @Query("select p from Personnel p where p.username = :username")
    <T>
    Optional<Personnel> findPersonnelByUsername(@Param("username") String username);

    @Query("select p from Personnel p where p.username = :currentUsername")
    Personnel findByUsername(@Param("currentUsername") String currentUsername);
}