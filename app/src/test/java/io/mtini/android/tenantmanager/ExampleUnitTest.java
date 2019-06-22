package io.mtini.android.tenantmanager;

import org.junit.Test;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void currencyTest(){
        NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
        moneyFormat.setCurrency(Currency.getInstance("USD"));

        //System.out.println(Currency.getAvailableCurrencies().contains("US")?"True":"False");

        Set<Currency> set = Currency.getAvailableCurrencies();
        Iterator<Currency> it = set.iterator();
        while(it.hasNext()){
            Currency cur = it.next();
            System.out.println(cur.toString()+" : "+cur.getCurrencyCode()+" : "+cur.getDisplayName()+" : "+cur.getSymbol());
        }

        System.out.println(Currency.getInstance("KES").getDisplayName());
        String moneys = moneyFormat.format(100.0000);
        System.out.println(moneys);

        assertTrue(Currency.getAvailableCurrencies().contains("USD"));

    }
}