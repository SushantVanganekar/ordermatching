package com.demo.ordermatchingengine.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="orderbook")
public class Order implements Comparable<Order> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long orderNumber;
	
	private String orderType;
	private String transactionType;
	private Double price;
	private int quantity;
	private Date orderTimestamp;
	private String status;
	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Order(Long orderNumber, String orderType, String transactionType, Double price, int quantity, Date timestamp,
			String status) {
		super();
		this.orderNumber = orderNumber;
		this.orderType = orderType;
		this.transactionType = transactionType;
		this.price = price;
		this.quantity = quantity;
		this.orderTimestamp = timestamp;
		this.status = status;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Date getTimestamp() {
		return orderTimestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.orderTimestamp = timestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderNumber == null) ? 0 : orderNumber.hashCode());
		result = prime * result + ((orderType == null) ? 0 : orderType.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + quantity;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((orderTimestamp == null) ? 0 : orderTimestamp.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (orderNumber == null) {
			if (other.orderNumber != null)
				return false;
		} else if (!orderNumber.equals(other.orderNumber))
			return false;
		if (orderType == null) {
			if (other.orderType != null)
				return false;
		} else if (!orderType.equals(other.orderType))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity != other.quantity)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (orderTimestamp == null) {
			if (other.orderTimestamp != null)
				return false;
		} else if (!orderTimestamp.equals(other.orderTimestamp))
			return false;
		if (transactionType == null) {
			if (other.transactionType != null)
				return false;
		} else if (!transactionType.equals(other.transactionType))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Order [orderNumber=" + orderNumber + ", orderType=" + orderType + ", transactionType=" + transactionType
				+ ", price=" + price + ", quantity=" + quantity + ", timestamp=" + orderTimestamp + ", status=" + status
				+ "]";
	}
	@Override
	public int compareTo(Order o) {
		if(this.price==o.getPrice())
			return this.getTimestamp().compareTo(o.getTimestamp());
		else
			return this.price.compareTo(o.getPrice());
	}
	
	

}
