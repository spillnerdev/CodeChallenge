package de.spillner.sales.data;

import java.math.BigDecimal;

/**
 * Other than the {@link Order} an {@link InvoicePosition} is the result of the tax calculation process.
 * It must still contain the amount and the name (like Order) but the number values are the gross total
 * (including tax) and the tax that was applied to it.
 *
 * @author Lukas Spillner
 */
public record InvoicePosition(Amount amount, String name, BigDecimal total, BigDecimal tax)
{
}