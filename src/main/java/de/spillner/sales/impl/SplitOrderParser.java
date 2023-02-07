package de.spillner.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de.spillner.sales.api.OrderParser;
import de.spillner.sales.data.Amount;
import de.spillner.sales.data.Order;
import de.spillner.sales.exception.OrderFormatException;

/**
 * @author Lukas Spillner
 */
public class SplitOrderParser
    implements OrderParser
{
  @Override
  public Order parse( String orderString )
  {
    String[] split = orderString.split( "\\s" );

    // Assumption: 1. value is always the amount
    var amount = parseInternal( () -> split[ 0 ], v -> v.matches( "\\d+" ), v -> new Amount( Integer.parseInt( v ) ) );
    // Assumption: String always ends with "at ${pricePerUnit}"
    var name = parseName( split );
    //Assumption last value is always the price perUnit
    var pricePerUnit = parseInternal( () -> split[ split.length - 1 ], v -> v.matches( "\\d+[.\\d]*" ),
        v -> new BigDecimal( v ).setScale( 2, RoundingMode.HALF_UP ) );
    return new Order( amount, name, pricePerUnit );
  }

  private <T> T parseInternal( Supplier<String> value, Predicate<String> test, Function<String, T> parse )
  {
    var v = value.get();

    if ( !test.test( v ) )
    {
      throw new OrderFormatException( "Unable to parse value {%s}".formatted( v ) );
    }

    return parse.apply( v );
  }

  private String parseName( String[] parts )
  {
    if ( parts.length - 2 < 1 )
    {
      throw new OrderFormatException(
          "Could not determine the name part for {%s}".formatted( Arrays.toString( parts ) ) );
    }

    return String.join( " ", Arrays.asList( parts ).subList( 1, parts.length - 2 ) );
  }
}