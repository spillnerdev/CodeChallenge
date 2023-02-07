package de.spillner.sales.impl;

import de.spillner.sales.api.OrderParser;
import de.spillner.sales.api.SaleCalculator;
import de.spillner.sales.data.Receipt;
import de.spillner.sales.data.tax.TaxClass;
import de.spillner.sales.data.tax.TaxRate;

import java.util.Collection;
import java.util.Map;

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
    throw new UnsupportedOperationException( "Not yet implemented" );
  }
}