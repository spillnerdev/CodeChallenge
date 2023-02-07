package de.spillner.sales.impl;

import de.spillner.sales.api.OrderParser;
import de.spillner.sales.data.Order;

import java.util.regex.Pattern;

/**
 * Implementation of {@link OrderParser} which uses a RegEx to dissect the string into its separate parts.
 *
 * @author Lukas Spillner
 */
public class RegExOrderParser
        implements OrderParser {
    private static final String DEFAULT_REGEX = "(\\d+)([^\\d]+)(\\d+[\\.\\d]*)";

    private static final RegExOrderParser DEFAULT = new RegExOrderParser(DEFAULT_REGEX);

    private final Pattern orderPattern;

    public RegExOrderParser(String regex) {
        this.orderPattern = Pattern.compile(regex);
    }

    @Override
    public Order parse(String orderString) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}