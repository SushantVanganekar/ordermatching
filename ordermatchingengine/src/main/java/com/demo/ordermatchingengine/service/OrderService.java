package com.demo.ordermatchingengine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.ordermatchingengine.model.MatchedOrder;
import com.demo.ordermatchingengine.model.Order;
import com.demo.ordermatchingengine.repository.OrderRepository;

@Service
public class OrderService {
	
	Logger log = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	OrderRepository orderRepository;

	private static Double lastTradedPrice = 100.0;

	public Order newOrder(Order order){
		log.info("newOrder :: START");
		order.setTimestamp(new Date());
		if(null!= order && null != order.getOrderType() && order.getOrderType().equals("MARKET") && null != order.getTransactionType()) {
			if(order.getTransactionType().equals("BUY"))
				order.setPrice(Double.MAX_VALUE);
			else
				order.setPrice(Double.MIN_VALUE);
			
		}
		Order savedOrder = orderRepository.save(order);
		log.info("newOrder :: END");
		return savedOrder;
		
	}
	
	public Map<String, List<Order>>  getCurrentOrderBook(){
		log.info("getCurrentOrderBook :: START");
		List<Order> orders = orderRepository.findAll();
		Map<String, List<Order>> sortedOrderMap = getSortedBuySellOrders(orders);
		log.info("getCurrentOrderBook :: END");
		return sortedOrderMap;
	}
	
	public List<Order> getAllOrders(){
		log.info("getAllOrders :: START");
		List<Order> orders = orderRepository.findAll();
		log.info("getAllOrders :: END");
		return orders;
	}
	
	
	public ArrayList<MatchedOrder> match(List<Order> orders){
		log.info("match :: START");
		
		Map<String, List<Order>> sortedOrdersMap = getSortedBuySellOrders(orders);
		List<Order> buyOrderSortedList = sortedOrdersMap.get("SortedBuyOrderList");
		List<Order> sellOrderSortedList = sortedOrdersMap.get("SortedSellOrderList");
		
		ArrayList<MatchedOrder> matchedOrdersList = new ArrayList<MatchedOrder>();
		sellOrderSortedList.forEach(sellOrder ->{
			ArrayList<MatchedOrder> matchedOrders = matchSellOrdersWithBuyOrders(sellOrder,buyOrderSortedList);
			if(null!=matchedOrders)
				matchedOrdersList.addAll(matchedOrders);
		});
		log.info("Matched Orders ::");
		matchedOrdersList.forEach(order -> log.info(order.toString()));
			
		//log.info("Buy Orders after Matching");
		buyOrderSortedList.forEach(buyOrder -> {
			if(buyOrder.getStatus().equals("MATCHED"))
				cancelOrder(buyOrder.getOrderNumber());
			if(buyOrder.getStatus().equals("UNMATCHED"))
				amendOrder(buyOrder);
		});	
		//log.info("Sell Orders after Matching");
		sellOrderSortedList.forEach(sellOrder -> {
			if(sellOrder.getStatus().equals("MATCHED"))
				cancelOrder(sellOrder.getOrderNumber());
			if(sellOrder.getStatus().equals("UNMATCHED"))
				amendOrder(sellOrder);
		});
		
		log.info("Updated Order Book");
		List<Order> updatedOrderList = orderRepository.findAll();
		updatedOrderList.forEach(order -> {
			log.info(order.toString());
			
		});
		
		log.info("match :: END");
		return matchedOrdersList;
	}
	
	private Map<String, List<Order>> getSortedBuySellOrders(List<Order> orders){
		log.info("getSortedBuySellOrders :: START");
		List<Order> buyOrderList = new ArrayList<Order>();
		List<Order> sellOrderList = new ArrayList<Order>();
		Map<String, List<Order>> sortedOrdersMap = new HashMap<String, List<Order>>();
		for(Order order : orders){
			String transactionType = order.getTransactionType();
			if(transactionType!=null){
				if(transactionType.equals("BUY"))
					buyOrderList.add(order);
				else
					sellOrderList.add(order);
			}
		}
		log.info("Buy Orders before sorting");
		buyOrderList.forEach(buyOrder -> log.info(buyOrder.toString()));
		
		log.info("Sell Orders before sorting");
		sellOrderList.forEach(sellOrder -> log.info(sellOrder.toString()));
		
		Collections.sort(buyOrderList, Collections.reverseOrder());
		Collections.sort(sellOrderList);
		
		log.info("Buy Orders after sorting");
		buyOrderList.forEach(buyOrder -> log.info(buyOrder.toString()));
		
		log.info("Sell Orders after sorting");
		sellOrderList.forEach(sellOrder -> log.info(sellOrder.toString()));
		
		sortedOrdersMap.put("SortedBuyOrderList", buyOrderList);
		sortedOrdersMap.put("SortedSellOrderList", sellOrderList);
		log.info("getSortedBuySellOrders :: END");
		return sortedOrdersMap;
	}
	
	
	private ArrayList<MatchedOrder> matchSellOrdersWithBuyOrders(Order sellOrder, List<Order> buyOrderSortedList){
		log.info("matchSellOrdersWithBuyOrders :: START");
		ArrayList<MatchedOrder> matchedOrders = new ArrayList<>();
		Double matchedSellPrice;
		for(Order buyOrder : buyOrderSortedList){
			if(sellOrder.getPrice() <= buyOrder.getPrice() && !buyOrder.getStatus().equals("MATCHED")){
				Integer sellQuantity = sellOrder.getQuantity();
				Integer buyQuantity = buyOrder.getQuantity();
				matchedSellPrice = calculateMatchedPrice(buyOrder.getOrderType(),sellOrder.getOrderType(),buyOrder.getPrice(), sellOrder.getPrice());
				//If sell order and buy order quantities are equal then mark both orders as MATCHED
				if(sellQuantity == buyQuantity){
					buyOrder.setStatus("MATCHED");
					sellOrder.setStatus("MATCHED");
					matchedOrders.add(new MatchedOrder(sellOrder.getOrderNumber(), buyOrder.getOrderNumber(), 
					sellOrder.getOrderType(), buyOrder.getOrderType(), matchedSellPrice, sellQuantity));
					break;
				}else if(sellQuantity > buyQuantity){
					// If sell quantity is large than buy quantity then buy order can be fully matched
					// and sell order will be partially matched
					buyOrder.setStatus("MATCHED");
					matchedOrders.add(new MatchedOrder(sellOrder.getOrderNumber(), buyOrder.getOrderNumber(), 
					sellOrder.getOrderType(), buyOrder.getOrderType(), matchedSellPrice, buyQuantity));
					sellOrder.setQuantity(sellQuantity-buyQuantity);
				}else{
					// If sell quantity is less than buy quantity then sell order can be fully matched
					// and buy order will be partially matched
					sellOrder.setStatus("MATCHED");
					buyOrder.setQuantity(buyQuantity-sellQuantity);
					matchedOrders.add(new MatchedOrder(sellOrder.getOrderNumber(), buyOrder.getOrderNumber(), 
					sellOrder.getOrderType(), buyOrder.getOrderType(), matchedSellPrice, sellQuantity));
					break;
				}
				
			}
		}
		log.info("matchSellOrdersWithBuyOrders :: END");
		return matchedOrders;
	}

	private Double calculateMatchedPrice(String buyOrderType, String sellOrderType, Double buyOrderPrice, Double sellOrderPrice) {
		Double matchedPrice = 0.0;
		if(buyOrderType.equals("LIMIT")) {
			matchedPrice = buyOrderPrice;
			lastTradedPrice = buyOrderPrice;
		}
		if(buyOrderType.equals("MARKET") && sellOrderType.equals("MARKET")){
			matchedPrice = lastTradedPrice;
		}
		if(buyOrderType.equals("MARKET") && sellOrderType.equals("LIMIT")){
			matchedPrice = sellOrderPrice;
			lastTradedPrice = sellOrderPrice;
		}		
		return matchedPrice;
	}
	public void cancelOrder(Long orderNumber) {
		log.info("cancelOrder :: START");
		orderRepository.deleteById(orderNumber);
		log.info("cancelOrder :: END");
	}

	public void amendOrder(Order order) {
		log.info("amendOrder :: START");
		orderRepository.save(order);
		log.info("amendOrder :: END");
	}

	public Optional<Order> findOrder(Long orderNumber) {
		log.info("findOrder :: START");
		Optional<Order> orderOptional = orderRepository.findById(orderNumber);
		log.info("findOrder :: END");
		return orderOptional;
	}
}
