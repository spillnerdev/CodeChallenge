package de.spillner.sales.exception;

/**
 * @author Lukas Spillner
 */
public class OrderFormatException
    extends RuntimeException
{
  public OrderFormatException( String message )
  {
    super( message );
  }

  public OrderFormatException( String message, Throwable cause )
  {
    super( message, cause );
  }

  public OrderFormatException( Throwable cause )
  {
    super( cause );
  }
}