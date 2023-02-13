package de.spillner.sales.data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A receipt is just a wrapper for invoice positions and the gross total + tax.
 *
 * @author Lukas Spillner
 */
public record Receipt(Collection<InvoicePosition> positions, BigDecimal grossTotal, BigDecimal totalTax)
{

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.ENGLISH) );
  private static final String ORDER_TEMPLATE =
      """
          %s
          Sales Taxes: %s
          Total: %s
          """;

  private static final String POSITION_FORMAT = "%d %s: %s";

  @Override
  public String toString()
  {
    String positions = positions().stream()
        .map( ip -> POSITION_FORMAT.formatted( ip.amount().amount(), ip.name(), DECIMAL_FORMAT.format( ip.total() ) ) )
        .collect( Collectors.joining( "\n" ) );

    return String.format( ORDER_TEMPLATE, positions, DECIMAL_FORMAT.format(  totalTax ), DECIMAL_FORMAT.format( grossTotal ) );
  }
}