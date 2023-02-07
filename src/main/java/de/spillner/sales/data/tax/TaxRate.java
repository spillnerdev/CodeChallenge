package de.spillner.sales.data.tax;

/**
 * The tax rate of goods wrapped into a data object. (DDD)
 * Assumption: The lawmaker will always keep the tax rate as a whole number.
 *
 * @author Lukas Spillner
 */
public record TaxRate(int rate)
{
}