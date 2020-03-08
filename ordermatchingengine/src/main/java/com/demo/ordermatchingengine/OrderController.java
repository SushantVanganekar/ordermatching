package com.demo.ordermatchingengine;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ordermatchingengine.model.Order;
import com.demo.ordermatchingengine.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	OrderService orderService;
		
	@PostMapping("/createOrder")
    @ResponseStatus(HttpStatus.CREATED)
	public void createOrder(@RequestBody Order order){	
		
		orderService.createOrder(order);
	}
	
	@GetMapping("/getAllOrders")
	public List<Order> getAllOrders(){
		
		return orderService.getAllOrders();
	}
	
	@GetMapping("/getAllMatchedOrders")
	public List<Order> getAllMatchedOrders(){
		List<Order> matchedOrders = Collections.emptyList();
		List<Order> orders = orderService.getAllOrders();
		if(null != orders){
			matchedOrders = orderService.matchOrders(orders);
		}
		return matchedOrders;
	}
	
	@DeleteMapping("/deleteOrder/{orderNumber}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteOrder(@PathVariable("orderNumber") Long orderNumber){
		Optional<Order> orderToBeUpdated = orderService.findOrder(orderNumber);
		if(orderToBeUpdated.isPresent())
			orderService.deleteOrder(orderNumber);
		else
			System.out.println("Order Not Found");
	}
	
	@PostMapping("/updateOrder")
    @ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOrder(@RequestBody Order order){	
		Optional<Order> orderToBeUpdated = orderService.findOrder(order.getOrderNumber());
		if(orderToBeUpdated.isPresent())
			orderService.updateOrder(order);
		else
			System.out.println("Order Not Found");
	}
}
