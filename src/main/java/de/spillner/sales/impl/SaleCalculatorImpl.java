package de.spillner.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.spillner.sales.api.OrderParser;
import de.spillner.sales.api.SaleCalculator;
import de.spillner.sales.data.InvoicePosition;
import de.spillner.sales.data.Order;
import de.spillner.sales.data.Receipt;
import de.spillner.sales.data.tax.TaxClass;
import de.spillner.sales.data.tax.TaxRate;

/**
 * @author Lukas Spillner
 * TODO: Docs!!!
 */
public class SaleCalculatorImpl
    implements SaleCalculator
{

  private final OrderParser parser;

  private final Map<TaxClass, TaxRate> taxRateMap;

  private final Collection<String> exemptGoods;

  public SaleCalculatorImpl( OrderParser parser, Map<TaxClass, TaxRate> taxRateMap,
      Collection<String> exemptGoods )
  {
    this.parser = parser;
    this.taxRateMap = taxRateMap;
    this.exemptGoods = exemptGoods;
  }

  @Override
  public Receipt calculateSale( Collection<String> orders )
  {
    var parsed = parser.parse( orders );

    var total = BigDecimal.ZERO;
    var tax = BigDecimal.ZERO;

    var positions = new ArrayList<InvoicePosition>();

    for ( var order : parsed )
    {
      var rate = getTaxRateForOder( order );

      var plainTotal = order.pricePerUnit().multiply( BigDecimal.valueOf( order.amount().amount() ) );
      var salesTax = plainTotal.multiply( BigDecimal.valueOf( rate.rate(), 0 ) )
          .divide( BigDecimal.valueOf( 100, 0 ), RoundingMode.CEILING )
          .setScale( 2, RoundingMode.CEILING );
      var gross = plainTotal.add( salesTax );

      tax = tax.add( salesTax );
      total = total.add( gross );
      positions.add( new InvoicePosition( order.amount(), order.goodName(), plainTotal.add( salesTax ), salesTax ) );
    }
    return new Receipt( positions, total, tax );
  }

  private TaxRate getTaxRateForOder( Order order )
  {
    int currentRate = 0;
    if ( isImported( order ) )
    {
      currentRate += taxRateMap.get( TaxClass.IMPORTED ).rate();
    }

    if ( !isExempt( order ) )
    {
      currentRate += taxRateMap.get( TaxClass.NORMAL ).rate();
    }
    return new TaxRate( currentRate );
  }

  private boolean isImported( Order order )
  {
    return order.goodName().contains( "imported" );
  }

  private boolean isExempt( Order order )
  {
    return exemptGoods.stream().anyMatch( s -> order.goodName().contains( s ) );
  }
}