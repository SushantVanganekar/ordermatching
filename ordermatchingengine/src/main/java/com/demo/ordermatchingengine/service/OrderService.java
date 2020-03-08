package com.demo.ordermatchingengine.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.ordermatchingengine.model.Order;
import com.demo.ordermatchingengine.repository.OrderRepository;

@Service
public class OrderService {
	
	Logger log = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	OrderRepository orderRepository;

	private static Double lastTradedPrice = 100.0;
	public Order createOrder(Order order){
		log.info("createOrder :: START");
		order.setTimestamp(new Date());
		log.info("createOrder :: END");
		return orderRepository.save(order);
		
	}
	
	public List<Order> getAllOrders(){
		log.info("getAllOrders :: START");
		List<Order> orders = orderRepository.findAll();
		log.info("getAllOrders :: END");
		return orders;
	}
	
	
	public ArrayList<Order> matchOrders(List<Order> orders){
		log.info("matchOrders :: START");
		LinkedHashMap<Long,Order> buyOrderMap = new LinkedHashMap<>();
		LinkedHashMap<Long,Order> sellOrderMap = new LinkedHashMap<>();
		
		for(Order order : orders){
			String transactionType = order.getTransactionType();
			if(transactionType!=null){
				if(transactionType.equals("BUY"))
					buyOrderMap.put(order.getOrderNumber(), order);
				else
					sellOrderMap.put(order.getOrderNumber(), order);
			}
		}
				
		log.info("Buy Orders before sorting");
		for(Entry<Long, Order> entry : buyOrderMap.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
		log.info("Sell Orders before sorting");
		for(Entry<Long, Order> entry : sellOrderMap.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
		LinkedHashMap<Long, Order> buyOrderSortedMap = buyOrderMap.entrySet()
				.stream()
				.sorted(Entry.<Long,Order>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new));
		LinkedHashMap<Long, Order> sellOrderSortedMap = sellOrderMap.entrySet()
				.stream()
				.sorted(Entry.<Long,Order>comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new));
		
		log.info("Buy Orders after sorting");
		for(Entry<Long, Order> entry : buyOrderSortedMap.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
		log.info("Sell Orders after sorting");
		for(Entry<Long, Order> entry : sellOrderSortedMap.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
		
		ArrayList<Order> matchedOrdersList = new ArrayList<Order>();
		for(Entry<Long, Order> entry : sellOrderSortedMap.entrySet()){
			ArrayList<Order> matchedOrders = matchSellOrdersWithBuyOrders(entry.getValue(),buyOrderSortedMap);
			if(null!=matchedOrders){
				matchedOrdersList.addAll(matchedOrders);
			}
		}
		
		log.info("Matched Orders ::");
		matchedOrdersList.forEach(order -> System.out.println(order));
			
		log.info("Buy Orders after Matching");
		for(Entry<Long, Order> entry : buyOrderSortedMap.entrySet()){
			Order order = entry.getValue();
			System.out.println(entry.getKey()+" "+order);
			if(order.getStatus().equals("MATCHED"))
				deleteOrder(order.getOrderNumber());
			if(order.getStatus().equals("UNMATCHED"))
				updateOrder(order);
		}
		
		log.info("Sell Orders after Matching");
		for(Entry<Long, Order> entry : sellOrderSortedMap.entrySet()){
			Order order = entry.getValue();
			System.out.println(entry.getKey()+" "+order);
			if(order.getStatus().equals("MATCHED"))
				deleteOrder(order.getOrderNumber());
			if(order.getStatus().equals("UNMATCHED"))
				updateOrder(order);
		}
		
		log.info("Updated Order Book");
		List<Order> updatedOrderList = orderRepository.findAll();
		updatedOrderList.forEach(order -> {
			System.out.println(order);
			
		});
		log.info("matchOrders :: END");
		return matchedOrdersList;
	}
	
	
	private ArrayList<Order> matchSellOrdersWithBuyOrders(Order sellOrder, LinkedHashMap<Long,Order> buyOrderSortedMap){
		log.info("matchSellOrdersWithBuyOrders :: START");
		ArrayList<Order> matchedOrders = new ArrayList<>();
		Double matchedSellPrice;
		for(Entry<Long, Order> buyEntry : buyOrderSortedMap.entrySet()){
			Order buyOrder = buyEntry.getValue();
			if(sellOrder.getPrice() <= buyOrder.getPrice() && !buyOrder.getStatus().equals("MATCHED")){
				Integer sellQuantity = sellOrder.getQuantity();
				Integer buyQuantity = buyOrder.getQuantity();
				
				//If sell order and buy order quantities are equal then mark both orders as MATCHED
				if(sellQuantity == buyQuantity){
					buyOrder.setStatus("MATCHED");
					sellOrder.setStatus("MATCHED");
					//buyOrder.setQuantity(0);
					//sellOrder.setQuantity(0);
					if(buyOrder.getOrderType().equals("MARKET")){
						buyOrder.setPrice(lastTradedPrice);
						sellOrder.setPrice(lastTradedPrice);
					}else{
						sellOrder.setPrice(buyOrder.getPrice());
					}
					matchedOrders.add(buyOrder);
					matchedOrders.add(sellOrder);
					
				}else if(sellQuantity > buyQuantity){
					// If sell quantity is large than buy quantity then buy order can be fully matched
					// and sell order will be partially matched
					if(buyOrder.getOrderType().equals("MARKET")){
						buyOrder.setPrice(lastTradedPrice);
						matchedSellPrice = lastTradedPrice;
					}else{
						matchedSellPrice = buyOrder.getPrice();
						lastTradedPrice=matchedSellPrice;
					}
					buyOrder.setStatus("MATCHED");
					//buyOrder.setQuantity(0);
					matchedOrders.add(buyOrder);
					matchedOrders.add(new Order(sellOrder.getOrderNumber(),sellOrder.getOrderType(),sellOrder.getTransactionType(),matchedSellPrice,buyQuantity,sellOrder.getTimestamp(),"MATCHED"));
					sellOrder.setQuantity(sellQuantity-buyQuantity);
				}else{
					// If sell quantity is less than buy quantity then sell order can be fully matched
					// and buy order will be partially matched
					if(buyOrder.getOrderType().equals("MARKET")){
						matchedSellPrice = lastTradedPrice;
					}else{
						matchedSellPrice = buyOrder.getPrice();
						lastTradedPrice=matchedSellPrice;
					}
					sellOrder.setStatus("MATCHED");
					//sellOrder.setQuantity(0);
					sellOrder.setPrice(matchedSellPrice);
					matchedOrders.add(sellOrder);
					buyOrder.setQuantity(buyQuantity-sellQuantity);
					matchedOrders.add(new Order(buyOrder.getOrderNumber(),buyOrder.getOrderType(), buyOrder.getTransactionType(), matchedSellPrice, sellQuantity, buyOrder.getTimestamp(), "MATCHED"));
					break;
				}
				
			}
		}
		log.info("matchSellOrdersWithBuyOrders :: END");
		return matchedOrders;
	}

	public void deleteOrder(Long orderNumber) {
		log.info("deleteOrder :: START");
		orderRepository.deleteById(orderNumber);
		log.info("deleteOrder :: END");
	}

	public void updateOrder(Order order) {
		log.info("updateOrder :: START");
		orderRepository.save(order);
		log.info("updateOrder :: END");
	}

	public Optional<Order> findOrder(Long orderNumber) {
		log.info("findOrder :: START");
		Optional<Order> orderOptional = orderRepository.findById(orderNumber);
		log.info("findOrder :: END");
		return orderOptional;
	}
}
