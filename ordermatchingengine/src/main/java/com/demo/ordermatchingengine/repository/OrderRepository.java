package com.demo.ordermatchingengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.ordermatchingengine.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
