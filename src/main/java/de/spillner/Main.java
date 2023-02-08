package de.spillner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import de.spillner.sales.api.SaleCalculator;
import de.spillner.sales.data.Order;
import de.spillner.sales.data.Receipt;
import de.spillner.sales.impl.RegExOrderParser;
import de.spillner.sales.impl.SaleCalculatorImpl;

/**
 * @author Lukas Spillner
 */
public class Main
{
  public static void main( String[] args ) throws IOException
  {
    // Very (and I mean **very**) basic CLI capabilities to actually use the application.
    // Everything else has been designed as some sort of library to be embedded into another application.
    if ( args.length == 0 )
    {
      throw new IllegalStateException( "Must specify a file with items!" );
    }

    // Get the file path...
    Path path = Paths.get( args[ 0 ] );
    // ... and read all lines
    try ( Stream<String> lines = Files.lines( path, StandardCharsets.UTF_8 ) )
    {
      // Parse them to Order objects
      List<Order> orders = lines.map( RegExOrderParser.DEFAULT::parse ).toList();

      // Calculate and print the results with default values.
      SaleCalculator calculator = new SaleCalculatorImpl();
      Receipt receipt = calculator.calculateSale( orders );
      System.out.println( receipt );
    }
  }
}