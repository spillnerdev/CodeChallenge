package de.spillner.sales.data.tax;

/**
 * There's a finite and fixed amount of taxes that can apply to a good.
 * I assume that the classes of taxes may never change but the value of the tax can.
 * For example a good can always be categorized as:
 * - Being taxed normally (10% in this task)
 * - Being taxed at a reduced rate (0% in this task)
 * - Be imported (5% in this task)
 *
 * PS: I rally wanted to point out that the byte-pattern for "taxed normally" and "imported"
 * match the use-case of the task perfectly.
 * 10 = 0b1010
 *  5 = 0b0101
 * 15 = 0b1111
 * This means if we wanted to get the total tax we could technically use bitwise OR for something like this:
 * <pre>
 *     int taxRate = 0;
 *
 *     if(isImported(good)){
 *         taxRate = taxRate | 5
 *     }
 *
 *     if(!isExempt(good)){
 *         taxRate = taxRate | 10
 *     }
 *     return taxRate;
 * </pre>
 * Not something useful for a production ready application, but it's something I find rather intriguing
 * and wanted to point it out.
 *
 * @author Lukas Spillner
 */
public enum TaxClass {
    REDUCED, //REDUCED may not be taxed in the current task but is subject to change.
    IMPORTED,
    NORMAL
}