package com.i2gether.escrow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.i2gether.escrow.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}