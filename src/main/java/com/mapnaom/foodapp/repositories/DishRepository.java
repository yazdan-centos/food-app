package com.mapnaom.foodapp.repositories;

import com.mapnaom.foodapp.models.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    boolean existsByName(String name);
    @Query("select d from Dish d where d.name like concat('%', :name, '%')")
    List<Dish> findAllByNameContains(@Param("name") String name);

    @Query("select (count(d) > 0) from Dish d where d.name = :name and d.price = :price")
    boolean existsDishByNameAndPrice(@Param("name") String name, @Param("price") Integer price);

    @Query("select (count(d) > 0) from Dish d where d.name = :name and d.price = :price and d.id <> :id")
    boolean existsDishByNameAndPriceAndIdNot(@Param("name") String name, @Param("price") Integer price, @Param("id") Long id);

}