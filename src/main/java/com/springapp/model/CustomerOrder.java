package com.springapp.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Jo on 28/09/2015.
 */

public class CustomerOrder implements Order {
    private static long orderNumberCounter = 0;
    private final long orderNumber = setOrderNumber();
    private DateTime dateOrderPlaced;
    private Map<Product, Integer> productsOrdered;
    private Map<Product, Map<DateTime, Integer>> productsDispatched;
    private Map<Product, Map<DateTime, Integer>> productsDelivered;
    private BigDecimal totalPrice;
    private Customer customer;

    public CustomerOrder(){
        this.dateOrderPlaced  = new DateTime();
        this.productsOrdered = null;
        this.productsDispatched = new HashMap<Product, Map<DateTime, Integer>>();
        this.productsDelivered = new HashMap<Product, Map<DateTime, Integer>>();
        this.totalPrice = new BigDecimal(0.00);
        this.customer = null;
    }

    public CustomerOrder(Map<Product, Integer> productsOrdered, BigDecimal totalPrice, Customer customer) {
        this.dateOrderPlaced  = new DateTime();
        this.productsOrdered = productsOrdered;
        this.productsDispatched = new HashMap<Product, Map<DateTime, Integer>>();
        this.productsDelivered = new HashMap<Product, Map<DateTime, Integer>>();
        this.totalPrice = totalPrice;
        this.customer = customer;
    }

    public CustomerOrder(DateTime dateOrderPlaced, Map<Product, Integer> productsOrdered, Map<Product, Map<DateTime, Integer>> productsDispatched, Map<Product, Map<DateTime, Integer>> productsDelivered, BigDecimal totalPrice, Customer customer) {
        this.dateOrderPlaced = dateOrderPlaced;
        this.productsOrdered = productsOrdered;
        this.productsDispatched = productsDispatched;
        this.productsDelivered = productsDelivered;
        this.totalPrice = totalPrice;
        this.customer = customer;
    }

    //sets the order number and increments the orderNumberCounter
    private long setOrderNumber() {
        orderNumberCounter += 1;
        return orderNumberCounter;
    }

    @Override
    public void orderProducts(List<Product> productsToOrder, Integer quantity) {
        int productStockLevel;
        for(Product product: productsToOrder){
            productStockLevel = product.adjustStockLevel(quantity);
            reorderProduct(product, productStockLevel);
        }
        WarehouseBrain.getWarehouseBrain().addCustomerOrder(this);
    }

    //reorders a product if below reorder level and not discontinued
    public void reorderProduct(Product product, int productStockLevel){
        if(productStockLevel < product.getReorderLevel() && !product.isDiscontinued()){
            new ProductOrder(product);
        }else if(productStockLevel == 0 && product.isDiscontinued()){
            ProductCatalogue.getCatalogue().removeProduct(product);
        }
        //else do nothing and let stock run down as the product is discontinued but still available.
    }

    @Override
    public Map<Product, Integer> getProductsOrdered() {
        return productsOrdered;
    }

    @Override
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public DateTime getDateOrderPlaced() {
        return dateOrderPlaced;
    }

    @Override
    public void setProductDispatched(Product product, Integer quantity) {
        if (quantity != 0) {
            Map<DateTime, Integer> dateQuantity = new HashMap<DateTime, Integer>();
            dateQuantity.put(new DateTime(), quantity);
            productsDispatched.put(product, dateQuantity);
        }
    }

    @Override
    public Map<Product, Map<DateTime, Integer>> getDispatchedProducts() {
        return productsDispatched;
    }

    @Override
    public void productDelivered(Product product, Integer quantity) {

    }

    @Override
    public Map<Product, Map<DateTime, Integer>> getDeliveredProducts() {
        return productsDelivered;
    }

    public static long getOrderNumberCounter(){
        return orderNumberCounter;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public Map<Product, Map<DateTime, Integer>> getProductsDelivered() {
        return productsDelivered;
    }

    public void setProductsDelivered(Map<Product, Map<DateTime, Integer>> productsDelivered) {
        this.productsDelivered = productsDelivered;
    }

    public BigDecimal getTotalPrice(BigDecimal totalPrice) {
        return totalPrice;
    }


}
