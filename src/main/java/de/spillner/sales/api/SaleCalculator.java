package de.spillner.sales.api;

import java.util.Collection;

import de.spillner.sales.data.Order;
import de.spillner.sales.data.Receipt;

/**
 * Implementations of {@link SaleCalculator} must produce a {@link Receipt}.
 *
 * @author Lukas Spillner
 */
@FunctionalInterface
public interface SaleCalculator
{

  /**
   * Calculates the tax and grand total of a collection of orders.
   * Note that it's completely legal to have multiple orders of the same good.
   * These will not be combined into a single order
   *
   * @param orders - The orders to process.
   * @return The receipt of all orders with the grand total and the total tax.
   */
  Receipt calculateSale( Collection<Order> orders );
}