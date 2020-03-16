package com.demo.ordermatchingengine;


import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.demo.ordermatchingengine.model.MatchedOrder;
import com.demo.ordermatchingengine.model.Order;
import com.demo.ordermatchingengine.repository.OrderRepository;
import com.demo.ordermatchingengine.service.OrderService;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTests {
	
	@InjectMocks
    OrderService orderService;
     
    @Mock
    OrderRepository orderRepository;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_GetCurrentOrderBook() {
    	//This method gets the current order book status in sorted order
    	//Both current order status and sorting functions gets tested here
    	List<Order> currentOrders = new ArrayList<Order>();
    	Order order1 = new Order(1L, "LIMIT", "BUY", 100.0, 1, new Date(), "UNMATCHED");
    	Order order2 = new Order(2L, "LIMIT", "BUY", 100.5, 10, new Date(), "UNMATCHED");
    	Order order3 = new Order(3L, "LIMIT", "BUY", 100.3, 5, new Date(), "UNMATCHED");
    	Order order4 = new Order(4L, "MARKET", "BUY", Double.MAX_VALUE, 3, new Date(), "UNMATCHED");
    	Order order5 = new Order(5L, "LIMIT", "BUY", 100.6, 2, new Date(), "UNMATCHED");
    	currentOrders.add(order1);
    	currentOrders.add(order2);
    	currentOrders.add(order3);
    	currentOrders.add(order4);
    	currentOrders.add(order5);
    	Order order6 = new Order(6L, "LIMIT", "SELL", 100.0, 1, new Date(), "UNMATCHED");
    	Order order7 = new Order(7L, "LIMIT", "SELL", 95.0, 1, new Date(), "UNMATCHED");
    	Order order8 = new Order(8L, "LIMIT", "SELL", 105.0, 1, new Date(), "UNMATCHED");
    	Order order9 = new Order(9L, "MARKET", "SELL", Double.MIN_VALUE, 1, new Date(), "UNMATCHED");
    	//Here sleep of 2 sec is added to have different timestamp for order #6 and #10 where price is same
    	//Order #6 will get priority over #10
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Order order10 = new Order(10L, "LIMIT", "SELL", 100.0, 1, new Date(), "UNMATCHED");
    	currentOrders.add(order6);
    	currentOrders.add(order7);
    	currentOrders.add(order8);
    	currentOrders.add(order9);
    	currentOrders.add(order10);
    	
    	when(orderRepository.findAll()).thenReturn(currentOrders);
    	Map<String, List<Order>> sortedOrderMap = orderService.getCurrentOrderBook();
    	List<Order> buyOrderList =  sortedOrderMap.get("SortedBuyOrderList");
    	List<Order> sellOrderList =  sortedOrderMap.get("SortedSellOrderList");
    	assertEquals(5, buyOrderList.size());
    	assertEquals(5, sellOrderList.size());
    	
    	assertEquals(4L, buyOrderList.get(0).getOrderNumber());
    	assertEquals(5L, buyOrderList.get(1).getOrderNumber());
    	assertEquals(2L, buyOrderList.get(2).getOrderNumber());
    	assertEquals(3L, buyOrderList.get(3).getOrderNumber());
    	assertEquals(1L, buyOrderList.get(4).getOrderNumber());
    	
    	assertEquals(9L, sellOrderList.get(0).getOrderNumber());
    	assertEquals(7L, sellOrderList.get(1).getOrderNumber());
    	assertEquals(6L, sellOrderList.get(2).getOrderNumber());
    	assertEquals(10L, sellOrderList.get(3).getOrderNumber());
    	assertEquals(8L, sellOrderList.get(4).getOrderNumber());
    	
    }

    @Test
    public void test_match() {
    	List<Order> currentOrders = new ArrayList<Order>();
    	Order order1 = new Order(1L, "LIMIT", "BUY", 100.0, 1, new Date(), "UNMATCHED");
    	Order order2 = new Order(2L, "LIMIT", "BUY", 100.5, 10, new Date(), "UNMATCHED");
    	Order order3 = new Order(3L, "LIMIT", "BUY", 100.3, 5, new Date(), "UNMATCHED");
    	Order order4 = new Order(4L, "MARKET", "BUY", Double.MAX_VALUE, 3, new Date(), "UNMATCHED");
    	Order order5 = new Order(5L, "LIMIT", "BUY", 100.6, 2, new Date(), "UNMATCHED");
    	currentOrders.add(order1);
    	currentOrders.add(order2);
    	currentOrders.add(order3);
    	currentOrders.add(order4);
    	currentOrders.add(order5);
    	Order order6 = new Order(6L, "LIMIT", "SELL", 100.0, 1, new Date(), "UNMATCHED");
    	Order order7 = new Order(7L, "LIMIT", "SELL", 95.0, 12, new Date(), "UNMATCHED");
    	Order order8 = new Order(8L, "LIMIT", "SELL", 105.0, 10, new Date(), "UNMATCHED");
    	Order order9 = new Order(9L, "MARKET", "SELL", Double.MIN_VALUE, 5, new Date(), "UNMATCHED");
    	//Here sleep of 2 sec is added to have different timestamp for order #6 and #10 where price is same
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Order order10 = new Order(10L, "LIMIT", "SELL", 100.0, 1, new Date(), "UNMATCHED");
    	currentOrders.add(order6);
    	currentOrders.add(order7);
    	currentOrders.add(order8);
    	currentOrders.add(order9);
    	currentOrders.add(order10);
    	
    	List<MatchedOrder> expectedMatchedOrders = new ArrayList<MatchedOrder>();
    	//new MatchedOrder(sellOrderNumber,buyOrderNumber,sellOrderType,buyOrderType,matchPrice,quantity)
    	expectedMatchedOrders.add(new MatchedOrder(9L,4L,"MARKET","MARKET",100.0,3));
    	expectedMatchedOrders.add(new MatchedOrder(9L,5L,"MARKET","LIMIT",100.6,2));
    	expectedMatchedOrders.add(new MatchedOrder(7L,2L,"LIMIT","LIMIT",100.5,10));
    	expectedMatchedOrders.add(new MatchedOrder(7L,3L,"LIMIT","LIMIT",100.3,2));
    	expectedMatchedOrders.add(new MatchedOrder(6L,3L,"LIMIT","LIMIT",100.3,1));
    	expectedMatchedOrders.add(new MatchedOrder(10L,3L,"LIMIT","LIMIT",100.3,1));
    	
    	ArrayList<MatchedOrder> matchedOrders = orderService.match(currentOrders);
    	assertTrue(expectedMatchedOrders.equals(matchedOrders));
    }
    
    @Test
    public void test_newOrder()
    {
    	Order order1 = new Order(1L, "LIMIT", "BUY", 100.0, 1, new Date(), "UNMATCHED");
         
        orderService.newOrder(order1);
         
        verify(orderRepository, times(1)).save(order1);
    }
    
	
	@Test
	public void test_amendOrder() {
		Order order1 = new Order(1L, "LIMIT", "BUY", 100.2, 1, new Date(), "UNMATCHED");
		orderService.amendOrder(order1);
		verify(orderRepository, times(1)).save(order1);
	}
    
    @Test
    public void test_cancelOrder()
    {
    	Order order = new Order(1L, "LIMIT", "BUY", 100.2, 1, new Date(), "UNMATCHED");
    	orderService.cancelOrder(order.getOrderNumber());
        verify(orderRepository, times(1)).deleteById(order.getOrderNumber());
    }
}


