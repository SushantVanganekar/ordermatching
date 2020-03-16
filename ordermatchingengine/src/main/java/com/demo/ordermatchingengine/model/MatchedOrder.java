package com.demo.ordermatchingengine.model;

public class MatchedOrder {

	private Long sellOrderNumber;
	private Long buyOrderNumber;
	private String sellOrderType;
	private String buyOrderType;
	private Double matchedPrice;
	private int matchedQuantity;
	public MatchedOrder() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MatchedOrder(Long sellOrderNumber, Long buyOrderNumber, String sellOrderType, String buyOrderType,
			Double matchedPrice, int matchedQuantity) {
		super();
		this.sellOrderNumber = sellOrderNumber;
		this.buyOrderNumber = buyOrderNumber;
		this.sellOrderType = sellOrderType;
		this.buyOrderType = buyOrderType;
		this.matchedPrice = matchedPrice;
		this.matchedQuantity = matchedQuantity;
	}
	public Long getSellOrderNumber() {
		return sellOrderNumber;
	}
	public void setSellOrderNumber(Long sellOrderNumber) {
		this.sellOrderNumber = sellOrderNumber;
	}
	public Long getBuyOrderNumber() {
		return buyOrderNumber;
	}
	public void setBuyOrderNumber(Long buyOrderNumber) {
		this.buyOrderNumber = buyOrderNumber;
	}
	public String getSellOrderType() {
		return sellOrderType;
	}
	public void setSellOrderType(String sellOrderType) {
		this.sellOrderType = sellOrderType;
	}
	public String getBuyOrderType() {
		return buyOrderType;
	}
	public void setBuyOrderType(String buyOrderType) {
		this.buyOrderType = buyOrderType;
	}
	public Double getMatchedPrice() {
		return matchedPrice;
	}
	public void setMatchedPrice(Double matchedPrice) {
		this.matchedPrice = matchedPrice;
	}
	public int getMatchedQuantity() {
		return matchedQuantity;
	}
	public void setMatchedQuantity(int matchedQuantity) {
		this.matchedQuantity = matchedQuantity;
	}
	@Override
	public String toString() {
		return "MatchedOrder [sellOrderNumber=" + sellOrderNumber + ", buyOrderNumber=" + buyOrderNumber
				+ ", sellOrderType=" + sellOrderType + ", buyOrderType=" + buyOrderType + ", matchedPrice="
				+ matchedPrice + ", matchedQuantity=" + matchedQuantity + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyOrderNumber == null) ? 0 : buyOrderNumber.hashCode());
		result = prime * result + ((buyOrderType == null) ? 0 : buyOrderType.hashCode());
		result = prime * result + ((matchedPrice == null) ? 0 : matchedPrice.hashCode());
		result = prime * result + matchedQuantity;
		result = prime * result + ((sellOrderNumber == null) ? 0 : sellOrderNumber.hashCode());
		result = prime * result + ((sellOrderType == null) ? 0 : sellOrderType.hashCode());
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
		MatchedOrder other = (MatchedOrder) obj;
		if (buyOrderNumber == null) {
			if (other.buyOrderNumber != null)
				return false;
		} else if (!buyOrderNumber.equals(other.buyOrderNumber))
			return false;
		if (buyOrderType == null) {
			if (other.buyOrderType != null)
				return false;
		} else if (!buyOrderType.equals(other.buyOrderType))
			return false;
		if (matchedPrice == null) {
			if (other.matchedPrice != null)
				return false;
		} else if (!matchedPrice.equals(other.matchedPrice))
			return false;
		if (matchedQuantity != other.matchedQuantity)
			return false;
		if (sellOrderNumber == null) {
			if (other.sellOrderNumber != null)
				return false;
		} else if (!sellOrderNumber.equals(other.sellOrderNumber))
			return false;
		if (sellOrderType == null) {
			if (other.sellOrderType != null)
				return false;
		} else if (!sellOrderType.equals(other.sellOrderType))
			return false;
		return true;
	}
	
	
}
