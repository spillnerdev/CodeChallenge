package de.spillner.sales.data;

import de.spillner.sales.data.tax.TaxClass;

import java.math.BigDecimal;

/**
 * An order is the amount of a certain good, the price per unit (before tax) and the tax classes that apply
 * to the good. The calculator can use this structured data to calculate the gross total and tax.
 *
 * @author Lukas Spillner
 */
public record Order(Amount amount, String goodName, BigDecimal pricePerUnit, TaxClass... taxClasses) {
}