package de.spillner.sales.api;

import de.spillner.sales.data.Order;

import java.util.Collection;

/**
 * Implementations of this class are able to parse a String to an {@link Order} object.
 *
 * @author Lukas Spillner
 */
@FunctionalInterface
public interface OrderParser
{
  /**
   * Helper method to parse a {@link Collection} of strings at once.
   * Uses {@link #parse(String)} internally
   *
   * @param orderStrings - The collection of strings to parse
   * @return The resulting collection of orders.
   * @see #parse(String)
   */
  default Collection<Order> parse( Collection<String> orderStrings )
  {
    return orderStrings.stream().map( this::parse ).toList();
  }

  /**
   * Parses a String to an {@link Order}.
   * It is encouraged (but not enforced) to throw an exception, if the String can not be parsed.
   *
   * @param orderString - The string to parse.
   * @return The resulting {@link Order} object.
   */
  Order parse( String orderString );
}