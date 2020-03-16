package com.demo.ordermatchingengine;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrdermatchingengineApplication.class})
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderControllerTests {

	   
    private static final String CONTENT_TYPE 
      = "application/json";
    
    private MockMvc mockMvc;		
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Before
    public void setup() throws Exception {
         this.mockMvc = webAppContextSetup(webApplicationContext)
           .build();
    }
    
    @Test
    public void testcase01_whenNewOrder_thenOk() throws Exception {
    	String orderJson1 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"BUY\",\"price\":100.0,\"quantity\":1,\"status\":\"UNMATCHED\"}";
    	String orderJson2 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"BUY\",\"price\":100.5,\"quantity\":10,\"status\":\"UNMATCHED\"}";
    	String orderJson3 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"BUY\",\"price\":100.3,\"quantity\":5,\"status\":\"UNMATCHED\"}";
    	String orderJson4 = "{\"orderType\":\"MARKET\",\"transactionType\":\"BUY\",\"price\":9999999.9,\"quantity\":3,\"status\":\"UNMATCHED\"}";
    	String orderJson5 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"BUY\",\"price\":100.6,\"quantity\":2,\"status\":\"UNMATCHED\"}";

    	String orderJson6 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"SELL\",\"price\":100.0,\"quantity\":1,\"status\":\"UNMATCHED\"}";
    	String orderJson7 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"SELL\",\"price\":95.0,\"quantity\":12,\"status\":\"UNMATCHED\"}";
    	String orderJson8 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"SELL\",\"price\":105.0,\"quantity\":10,\"status\":\"UNMATCHED\"}";
    	String orderJson9 = "{\"orderType\":\"MARKET\",\"transactionType\":\"SELL\",\"price\":0.0,\"quantity\":5,\"status\":\"UNMATCHED\"}";
    	String orderJson10 = "{\"orderType\":\"LIMIT\",\"transactionType\":\"SELL\",\"price\":100.0,\"quantity\":1,\"status\":\"UNMATCHED\"}";
    	
        this.mockMvc.perform(post("/newOrder")
          .contentType(CONTENT_TYPE)
          .content(orderJson1))
          .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson2))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson3))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson4))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson5))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson6))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson7))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson8))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson9))
                .andExpect(status().isCreated());  
        this.mockMvc.perform(post("/newOrder")
                .contentType(CONTENT_TYPE)
                .content(orderJson10))
                .andExpect(status().isCreated());
    }
    
    @Test
    public void testcase02_whenGetAllOrders_thenOk() throws Exception{
    	this.mockMvc.perform(get("/getCurrentOrderBook"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(CONTENT_TYPE))
        ;
    }
    
    @Test
    public void testcase03_whenGetAllMatchedOrders_thenOk() throws Exception{
    	this.mockMvc.perform(get("/match"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(jsonPath("$", hasSize(6)))
        ;
    }
    
    
    @Test
    public void testcase05_whenCancelOrder_thenOk() throws Exception{
    	this.mockMvc.perform(delete("/cancelOrder/1"))
        .andExpect(status().isOk())
        ;
    }
    
    @Test
    public void testcase06_whenAmendOrderThen_Ok() throws Exception{
    	String orderUpdateJson = "{\"orderNumber\":8,\"orderType\":\"LIMIT\",\"transactionType\":\"SELL\",\"price\":106.0,\"quantity\":10,\"status\":\"UNMACTHED\"}";
        this.mockMvc.perform(post("/amendOrder")
        .contentType(CONTENT_TYPE)
        .content(orderUpdateJson))
        .andExpect(status().isAccepted());
    }
}

