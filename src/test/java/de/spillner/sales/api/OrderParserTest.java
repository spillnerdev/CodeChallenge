package de.spillner.sales.api;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.spillner.sales.data.Order;
import de.spillner.sales.exception.OrderFormatException;
import de.spillner.sales.impl.RegExOrderParser;
import de.spillner.sales.impl.SplitOrderParser;

/**
 * @author Lukas Spillner
 */
class OrderParserTest
{

  private static Stream<Arguments> validSource()
  {
    Map<String, Order> testSetOne = Map.of(
        "1 book at 12.49", Order.of( 1, "book", 12.49 ),
        "1 music CD at 14.99", Order.of( 1, "music CD", 14.99 ),
        "1 chocolate bar at 0.85", Order.of( 1, "chocolate bar", 0.85 )
    );

    Map<String, Order> testSetTwo = Map.of(
        "2 potatoes at 52", Order.of( 2, "potatoes", 52.00 )
    );
    Map<String, Order> testSetThree = Map.of(
        "1 imported box of chocolate at 10.00", Order.of( 1, "imported box of chocolate", 10.00 ),
        "1 imported bottle of perfume at 47.00", Order.of( 1, "imported bottle of perfume", 47.00 )
    );
    return Stream.of(
        Arguments.of( RegExOrderParser.DEFAULT, testSetOne ),
        Arguments.of( RegExOrderParser.DEFAULT, testSetTwo ),
        Arguments.of( RegExOrderParser.DEFAULT, testSetThree ),
        Arguments.of( new SplitOrderParser(), testSetOne ),
        Arguments.of( new SplitOrderParser(), testSetTwo ),
        Arguments.of( new SplitOrderParser(), testSetThree )
    );
  }

  @ParameterizedTest
  @MethodSource( "validSource" )
  void testParseValid( OrderParser parser, Map<String, Order> stringExpectedMap )
  {
    stringExpectedMap.entrySet().stream()
        .map( e -> Map.entry( parser.parse( e.getKey() ), e.getValue() ) )
        .forEach( e -> assertThat( e.getKey(), equalTo( e.getValue() ) ) );
  }

  @ParameterizedTest
  @MethodSource( "validSource" )
  void testParseBulk( OrderParser parser, Map<String, Order> stringExpectedMap )
  {
    Collection<Order> orders = parser.parse( stringExpectedMap.keySet() );
    assertThat( orders, containsInAnyOrder( stringExpectedMap.values().toArray() ) );
  }

  private static Stream<Arguments> malformedSource()
  {
    return Stream.of(
        Arguments.of( RegExOrderParser.DEFAULT ),
        Arguments.of( new SplitOrderParser() )
    );
  }

  @ParameterizedTest
  @MethodSource( "malformedSource" )
  void testMalformedOrderString( OrderParser parser )
  {
    String orderString = "Some gibberish which must cause an exception";
    assertThrowsExactly( OrderFormatException.class, () -> parser.parse( orderString ) );

    String other = "1 imported box of chocolate at 4.20 with some appendix";
    assertThrowsExactly( OrderFormatException.class, () -> parser.parse( other ) );

    String tooShort = "1 15.23";
    assertThrowsExactly( OrderFormatException.class, () -> parser.parse( tooShort ) );

    String whitespaces = "1                           15.23";
    assertThrowsExactly( OrderFormatException.class, () -> parser.parse( whitespaces ) );

  }
}