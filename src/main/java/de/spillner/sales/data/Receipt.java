package de.spillner.sales.data;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * A receipt is just a wrapper for invoice positions and the gross total + tax.
 *
 * @author Lukas Spillner
 */
public record Receipt(Collection<InvoicePosition> positions, BigDecimal grossTotal, BigDecimal totalTax)
{
  private static final String POSITION_FORMAT = "%d %s %.2f";

  @Override
  public String toString()
  {
    var sb = new StringBuilder();
    positions().stream()
        .map( ip -> POSITION_FORMAT.formatted( ip.amount().amount(), ip.name(), ip.total() ) )
        .forEach( sb::append );
    sb.append( "Sales Taxes: %.2f\n".formatted( totalTax() ) )
        .append( "Total: %.2f".formatted( grossTotal() ) );

    return sb.toString();
  }
}