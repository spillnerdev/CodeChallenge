package de.spillner.sales.api;

import de.spillner.sales.data.Receipt;

import java.util.Arrays;
import java.util.Collection;

/**
 * Implementations of {@link SaleCalculator} must produce a {@link Receipt}.
 * @author Lukas Spillner
 */
@FunctionalInterface
public interface SaleCalculator {

    /**
     * In case we receive only a single string we assume that's in the format of "order\n order\n order"
     * and split accordingly.
     *
     * @param orderString - The entire collections of orders in a single String.
     * @return The receipt calculated by {@link #calculateSale(Collection)}
     * @see #calculateSale(Collection)
     */
    default Receipt calculateSale(String orderString) {
        Collection<String> orders = Arrays.asList(orderString.split("\n"));
        return calculateSale(orders);
    }

    /**
     * Calculates the tax and grand total of a collection of orders.
     * Note that it's completely legal to have multiple orders of the same good.
     * These will not be combined into a single order
     *
     * @param orders - The orders to process. Expected format: "${amount} ${nameOfGood} ${pricePerUnit}"
     * @return The receipt of all orders with the grand total and the total tax.
     */
    Receipt calculateSale(Collection<String> orders);
}