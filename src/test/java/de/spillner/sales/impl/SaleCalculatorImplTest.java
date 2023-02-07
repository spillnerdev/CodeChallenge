package de.spillner.sales.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.spillner.sales.api.SaleCalculator;
import de.spillner.sales.data.Receipt;
import de.spillner.sales.data.tax.TaxClass;
import de.spillner.sales.data.tax.TaxRate;

/**
 * @author Lukas Spillner
 */
class SaleCalculatorImplTest
{
  private Map<TaxClass, TaxRate> currentTaxRate;

  private Collection<String> exemptGoods = List.of( "chocolate", "pills", "book" );

  @BeforeEach
  void setUp()
  {
    currentTaxRate = new EnumMap<>( TaxClass.class );
    currentTaxRate.put( TaxClass.REDUCED, new TaxRate( 0 ) );
    currentTaxRate.put( TaxClass.IMPORTED, new TaxRate( 5 ) );
    currentTaxRate.put( TaxClass.NORMAL, new TaxRate( 10 ) );
  }

  @Test
  void testBasicCalculation()
  {
    String order = """
        1 book at 12.49
        1 music CD at 14.99
        1 chocolate bar at 0.85
        """;
    SaleCalculator calc = new SaleCalculatorImpl( RegExOrderParser.DEFAULT, currentTaxRate, exemptGoods );
    Receipt receipt = calc.calculateSale( order );

    assertThat( receipt, notNullValue() );
    assertThat( receipt.grossTotal(), equalTo( BigDecimal.valueOf( 29.83 ).setScale( 2, RoundingMode.HALF_UP ) ) );
    assertThat( receipt.totalTax(), equalTo( BigDecimal.valueOf( 1.50 ).setScale( 2, RoundingMode.CEILING ) ) );
    assertThat( receipt.toString(), equalTo(
        """
            1 book: 12.49
            1 music CD: 16.49
            1 chocolate bar 0.85
            Sales Taxes: 1.50
            Total: 29.83
            """

    ) );
  }
}