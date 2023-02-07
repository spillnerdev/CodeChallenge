package de.spillner.sales.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.spillner.sales.api.OrderParser;
import de.spillner.sales.data.Amount;
import de.spillner.sales.data.Order;
import de.spillner.sales.exception.OrderFormatException;

/**
 * Implementation of {@link OrderParser} which uses a RegEx to dissect the string into its separate parts.
 *
 * @author Lukas Spillner
 */
public class RegExOrderParser
    implements OrderParser
{
  private static final String DEFAULT_REGEX = "(\\d+)([^\\d]+)(\\d+[\\.\\d]*)";

  public static final RegExOrderParser DEFAULT = new RegExOrderParser( DEFAULT_REGEX );

  private final Pattern orderPattern;
  private final String regex;

  public RegExOrderParser( String regex )
  {
    this.regex = regex;
    this.orderPattern = Pattern.compile( regex );
  }

  @Override
  public Order parse( String orderString )
  {
    Matcher matcher = orderPattern.matcher( orderString );

    if ( !matcher.matches() )
    {
      throw new OrderFormatException(
          "The order string {%s} does not adhere to the expected format of the regex: {%s}".formatted( orderString,
              regex ) );
    }

    var amount = new Amount( Integer.parseInt( matcher.group( 1 ) ) );
    var name = matcher.group( 2 ).trim().replace( " at", "" );
    var pricePerUnit = new BigDecimal( matcher.group( 3 ) ).setScale( 2, RoundingMode.HALF_UP );
    return new Order( amount, name, pricePerUnit );
  }
}