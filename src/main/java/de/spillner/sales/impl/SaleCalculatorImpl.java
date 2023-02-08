package de.spillner.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
  private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf( 100L, 0 );
  private static final BigDecimal SALES_INCREMENT = BigDecimal.valueOf( 0.05 );

  private final Map<TaxClass, TaxRate> taxRateMap;

  private final Collection<String> exemptGoods;

  public SaleCalculatorImpl( Map<TaxClass, TaxRate> taxRateMap,
      Collection<String> exemptGoods )
  {
    this.taxRateMap = taxRateMap;
    this.exemptGoods = exemptGoods;
  }

  @Override
  public Receipt calculateSale( Collection<Order> orders )
  {
    var total = BigDecimal.ZERO;
    var tax = BigDecimal.ZERO;

    var positions = new ArrayList<InvoicePosition>();

    for ( var order : orders )
    {
      var rate = getTaxRateForOder( order );

      var plainTotal = order.pricePerUnit().multiply( BigDecimal.valueOf( order.amount().amount() ) );
      var salesTax = plainTotal.multiply( BigDecimal.valueOf( rate.rate(), 0 ) )
          .divide( ONE_HUNDRED, RoundingMode.CEILING )
          .divide( SALES_INCREMENT, 0, RoundingMode.UP )
          .multiply( SALES_INCREMENT );
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