package de.spillner.sales.data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A receipt is just a wrapper for invoice positions and the gross total + tax.
 *
 * @author Lukas Spillner
 */
public record Receipt(Collection<InvoicePosition> positions, BigDecimal grossTotal, BigDecimal totalTax)
{

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
  private static final String ORDER_TEMPLATE =
      """
          %s
          Sales Taxes: %.2f
          Total: %.2f
          """;

  private static final String POSITION_FORMAT = "%d %s: %s";

  @Override
  public String toString()
  {
    String positions = positions().stream()
        .map( ip -> POSITION_FORMAT.formatted( ip.amount().amount(), ip.name(), DECIMAL_FORMAT.format( ip.total() ) ) )
        .collect( Collectors.joining( "\n" ) );

    return String.format( ORDER_TEMPLATE, positions, totalTax, grossTotal );
  }
}