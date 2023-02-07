package de.spillner.sales.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A receipt is just a wrapper for invoice positions and the gross total + tax.
 *
 * @author Lukas Spillner
 */
public record Receipt(Collection<InvoicePosition> positions, BigDecimal grossTotal, BigDecimal totalTax)
{
  private static final String ORDER_TEMPLATE =
      """
          %s
          Sales Taxes: %.2f
          Total: %.2f
          """;

  private static final String POSITION_FORMAT = "%d %s %.2f";

  @Override
  public String toString()
  {
    String positions = positions().stream()
        .map( ip -> POSITION_FORMAT.formatted( ip.amount().amount(), ip.name(), ip.total() ) )
        .collect( Collectors.joining( "\n" ) );
    return String.format( ORDER_TEMPLATE, positions, totalTax, grossTotal );
  }
}