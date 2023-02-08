package de.spillner.sales.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.spillner.sales.api.SaleCalculator;
import de.spillner.sales.data.Order;
import de.spillner.sales.data.Receipt;
import de.spillner.sales.data.tax.TaxClass;
import de.spillner.sales.data.tax.TaxRate;

/**
 * @author Lukas Spillner
 */
class SaleCalculatorImplTest
{
  private Map<TaxClass, TaxRate> currentTaxRate;

  private final Collection<String> exemptGoods = List.of( "chocolate", "pills", "book" );

  @BeforeEach
  void setUp()
  {
    currentTaxRate = new EnumMap<>( TaxClass.class );
    currentTaxRate.put( TaxClass.REDUCED, new TaxRate( 0 ) );
    currentTaxRate.put( TaxClass.IMPORTED, new TaxRate( 5 ) );
    currentTaxRate.put( TaxClass.NORMAL, new TaxRate( 10 ) );
  }

  private static Stream<Arguments> basicCalcSource()
  {
    return Stream.of(
        Arguments.of(
            List.of(
                Order.of( 1, "imported box of chocolates", 10.00 ),
                Order.of( 1, "imported bottle of perfume", 47.50 )
            ), 65.15
            , 7.65
            , """
                1 imported box of chocolates: 10.50
                1 imported bottle of perfume: 54.65
                Sales Taxes: 7.65
                Total: 65.15
                """
        ),
        Arguments.of(
            List.of(
                Order.of( 1, "book", 12.49 ),
                Order.of( 1, "music CD", 14.99 ),
                Order.of( 1, "chocolate bar", 0.85 )
            ), 29.83
            , 1.50
            , """
                1 book: 12.49
                1 music CD: 16.49
                1 chocolate bar: 0.85
                Sales Taxes: 1.50
                Total: 29.83
                """
        ),
        Arguments.of(
            List.of(
                Order.of( 1, "imported bottle of perfume", 27.99 ),
                Order.of( 1, "bottle of perfume", 18.99 ),
                Order.of( 1, "packet of headache pills", 9.75 ),
                Order.of( 1, "box of imported chocolates", 11.25 )
            ), 74.68
            , 6.70
            , """
                1 imported bottle of perfume: 32.19
                1 bottle of perfume: 20.89
                1 packet of headache pills: 9.75
                1 box of imported chocolates: 11.85
                Sales Taxes: 6.70
                Total: 74.68
                """
        )
    );
  }

  @ParameterizedTest
  @MethodSource( "basicCalcSource" )
  void testBasicCalculation( Collection<Order> orders, double grossTotal, double totalTax, String expectedReceipt )
  {
    SaleCalculator calc = new SaleCalculatorImpl( currentTaxRate, exemptGoods );
    Receipt receipt = calc.calculateSale( orders );

    assertThat( receipt, notNullValue() );
    assertThat( receipt.grossTotal(), equalTo( BigDecimal.valueOf( grossTotal ).setScale( 2, RoundingMode.HALF_UP ) ) );
    assertThat( receipt.totalTax(), equalTo( BigDecimal.valueOf( totalTax ).setScale( 2, RoundingMode.CEILING ) ) );
    assertThat( receipt.toString(), equalTo( expectedReceipt ) );
  }
}