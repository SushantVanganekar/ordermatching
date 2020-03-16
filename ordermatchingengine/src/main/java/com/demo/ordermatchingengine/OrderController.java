package com.demo.ordermatchingengine;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ordermatchingengine.exceptions.OrderNotFoundException;
import com.demo.ordermatchingengine.model.MatchedOrder;
import com.demo.ordermatchingengine.model.Order;
import com.demo.ordermatchingengine.service.OrderService;

@RestController
public class OrderController {

	Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	OrderService orderService;
		
	@PostMapping("/newOrder")
    @ResponseStatus(HttpStatus.CREATED)
	public void newOrder(@RequestBody Order order){	
		log.info("newOrder :: START");
		orderService.newOrder(order);
		log.info("newOrder :: END");
	}
	
	@GetMapping("/getCurrentOrderBook")
	public Map<String, List<Order>> getCurrentOrderBook(){
		log.info("getCurrentOrderBook :: START");
		Map<String, List<Order>> currentOrderBook =  orderService.getCurrentOrderBook();
		log.info("getCurrentOrderBook :: END");
		return currentOrderBook;
	}
	
	@GetMapping("/match")
	public List<MatchedOrder> match(){
		log.info("match :: START");
		List<MatchedOrder> matchedOrders = Collections.emptyList();
		List<Order> orders = orderService.getAllOrders();
		if(null != orders){
			matchedOrders = orderService.match(orders);
		}
		log.info("match :: END");
		return matchedOrders;
	}
	
	@DeleteMapping("/cancelOrder/{orderNumber}")
	@ResponseStatus(HttpStatus.OK)
	public void cancelOrder(@PathVariable("orderNumber") Long orderNumber) throws OrderNotFoundException{
		log.info("cancelOrder :: START");
		Optional<Order> orderOptional = orderService.findOrder(orderNumber);
		if(orderOptional.isPresent())
			orderService.cancelOrder(orderNumber);
		else {
			log.info("Order Not Found");
			throw new OrderNotFoundException("Order Not Found");
		}
		log.info("cancelOrder :: END");
	}
	
	@PostMapping("/amendOrder")
    @ResponseStatus(HttpStatus.ACCEPTED)
	public void amendOrder(@RequestBody Order order) throws OrderNotFoundException{
		log.info("amendOrder :: START");
		Optional<Order> orderToBeUpdated = orderService.findOrder(order.getOrderNumber());
		if(orderToBeUpdated.isPresent())
			orderService.amendOrder(order);
		else {
			log.info("Order Not Found");
			throw new OrderNotFoundException("Order Not Found");
		}
		log.info("amendOrder :: END");
	}
}
