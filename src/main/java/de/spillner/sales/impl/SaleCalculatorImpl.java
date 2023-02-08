package de.spillner.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.spillner.sales.api.SaleCalculator;
import de.spillner.sales.data.InvoicePosition;
import de.spillner.sales.data.Order;
import de.spillner.sales.data.Receipt;
import de.spillner.sales.data.tax.TaxClass;
import de.spillner.sales.data.tax.TaxRate;

/**
 * This implementation of the {@link SaleCalculator} calculates the gross total amount and the applied tax
 * of every given order by {@link #calculateSale(Collection)} and returns a {@link Receipt} object.
 *
 * @author Lukas Spillner
 */
public class SaleCalculatorImpl
    implements SaleCalculator
{
  /**
   * There no "BigDecimal#HUNDRED" so we build our own
   */
  private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf( 100L, 0 );
  /**
   * The "steps" of our tax are always in increments of 0.05.
   */
  private static final BigDecimal SALES_INCREMENT = BigDecimal.valueOf( 0.05 );

  /**
   * Some default exempt goods. Can be overwritten with the constructor SaleCalculatorImpl(Map,Collection)
   */
  private static final Collection<String> DEFAULT_EXEMPT_GOODS = List.of( "chocolate", "pills", "book" );
  /**
   * Default set of TaxRates for each tax class.
   */
  private static final Map<TaxClass, TaxRate> DEFAULT_TAX_RATES = Map.of(
      TaxClass.REDUCED, new TaxRate( 0 ),
      TaxClass.IMPORTED, new TaxRate( 5 ),
      TaxClass.NORMAL, new TaxRate( 10 )
  );

  private final Map<TaxClass, TaxRate> taxRateMap;

  private final Collection<String> exemptGoods;

  /**
   * Default constructor which uses default values as its tax rates and exempt goods.
   */
  public SaleCalculatorImpl()
  {
    this( DEFAULT_TAX_RATES, DEFAULT_EXEMPT_GOODS );
  }

  /**
   * Constructs a new calculator with the given tax rates per tax class and the name of exempt goods.
   * Note that the name of the exempt good must only be contained within the order somewhere to be exempt from the tax.
   *
   * @param taxRateMap - The tax rate map governing the tax rate per tax class (e.g. [TaxClass.IMPORTED => 5])
   * @param exemptGoods - The name of all exempt goods.
   */
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
          // we divide and later multiply by the sales_increment so that we get only increments of 0.05 and always round up.
          .divide( SALES_INCREMENT, 0, RoundingMode.UP )
          .multiply( SALES_INCREMENT );
      var gross = plainTotal.add( salesTax );

      tax = tax.add( salesTax );
      total = total.add( gross );
      // Add the currently calculated position to the list of positions.
      positions.add( new InvoicePosition( order.amount(), order.goodName(), plainTotal.add( salesTax ), salesTax ) );
    }
    // And return everything in a receipt.
    // Total and tax could also be evaluated on the positions alone.
    // But it's the good old tradeoff between time or space.
    // The few more bytes of RAM to store the total and tax again vs the time it takes to compute the total and tax
    // every time it's required. I chose the first this time.
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