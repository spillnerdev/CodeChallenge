package de.spillner.sales.data;

/**
 * Wraps the "amount" of items of an order into a data object. (DDD)
 *
 * PS: This is a personal preference. I've been burned to many times by method signatures like:
 * <pre>public double doSomething(int, double, float, float, int, int, float, double, int)</pre>
 *
 * @author Lukas Spillner
 */
public record Amount(int amount)
{
}