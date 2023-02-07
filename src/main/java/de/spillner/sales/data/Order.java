package de.spillner.sales.data;

import java.math.BigDecimal;

/**
 * An order is the amount of a certain good, the price per unit (before tax).
 * The calculator can use this structured data to calculate the gross total and tax.
 *
 * @author Lukas Spillner
 */
public record Order(Amount amount, String goodName, BigDecimal pricePerUnit)
{

  public static Order of( int amount, String name, double pricePerUnit )
  {
    return new Order( new Amount( amount ), name, BigDecimal.valueOf( pricePerUnit ) );
  }

}