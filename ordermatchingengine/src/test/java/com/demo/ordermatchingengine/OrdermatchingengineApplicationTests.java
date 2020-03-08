package com.demo.ordermatchingengine;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrderController.class)
@WebAppConfiguration
public class OrdermatchingengineApplicationTests {

   
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
	    public void whenCreateOrder_thenOk() throws Exception {
	        String employeeJson = "{\"name\":\"john\"}";      
	        
	        this.mockMvc.perform(post("/createOrder")
	          .contentType(CONTENT_TYPE)
	          .content(employeeJson))
	          .andExpect(status().isCreated());
	        
	        this.mockMvc.perform(get("/employees"))
	          .andExpect(status().isOk())
	          .andExpect(content().contentType(CONTENT_TYPE))
	          .andExpect(jsonPath("$", hasSize(2)))
	          .andExpect(jsonPath("$[0].name", is("ana")))
	          .andExpect(jsonPath("$[1].name", is("john")));      
	    }
}
