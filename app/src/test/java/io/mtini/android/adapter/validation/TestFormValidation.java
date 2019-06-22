package io.mtini.android.adapter.validation;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFormValidation {

    String phonePattern  = "^(\\+)[1-9]{1}([0-9][\\s]*){9,16}$";

    //String phonePattern2 = "^\\(\\+[1-9]{1}[0-9]{0,2}\\)+[1-9]{1}+([0-9]){4,16}$";
    //String phonePattern2 = "^(\\+)[1-9]{1}[0-9]{0,2}+/ /+[1-9]{1}+([0-9]){4,16}$";
    String phonePattern2 = "^(\\+[1-9]{1}[0-9]{0,2})(\\ )(([0-9][\\s]*){9,16})$";

    String emailPattern = "^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

    String stringLegthPattern = "^[A-Z0-9._%+\\-+/ /]{3,20}$";//"^[A-Z0-9._%-]{3,20}$";

    String currencyPattern = "^[0-9]{1,20}+\\.[0-9]{1,2}$";
    @Test
    public void testCurrencyValidation(){
        assert validatePattern("10.00",currencyPattern);

        assert validatePattern("0.00",currencyPattern);

        assert !validatePattern("10.000",currencyPattern);

        assert !validatePattern("-10.00",currencyPattern);

        assert !validatePattern("",currencyPattern);
    }


    @Test
    public void testEmailValidation(){
        assert validatePattern("kaniu@mtini.io",emailPattern);

        assert !validatePattern("kaniumtini.io",emailPattern);

        assert !validatePattern("kaniu@mtiniio",emailPattern);

        assert validatePattern("kaniu@mtini.io.ke",emailPattern);

        assert !validatePattern("",emailPattern);
    }

    @Test
    public void testPhoneValidation(){
        assert validatePattern("+254908724112",phonePattern);

        assert !validatePattern("+0254908724112",phonePattern);

        assert !validatePattern("",phonePattern);
    }


    @Test
    public void testPhoneValidationType2(){

        Pattern pattern = Pattern.compile(phonePattern2);

        Matcher m = pattern.matcher("+254 09 087 24112");
        if (m.find()) {
            //int g = m.groupCount();
            for(int i = 0 ; i < m.groupCount();i++ ){
                System.out.println(i+"  "+m.group(i));
            }

        }

        assert validatePattern("+254 908724112",phonePattern2);

        assert !validatePattern("(+254)908724112",phonePattern2);

        assert validatePattern("+254 0908 724112",phonePattern2);

        assert !validatePattern("",phonePattern2);




    }

    @Test
    public void testStringLengthValidation(){

        assert !validatePattern("bl",stringLegthPattern);

        assert validatePattern("blah blah blah",stringLegthPattern);

        assert !validatePattern("blah blah blah blah b",stringLegthPattern);

        assert !validatePattern("blah blah blah*",stringLegthPattern);

        assert validatePattern("blah_blah-blah",stringLegthPattern);

        assert !validatePattern("blah_blah-blah?",stringLegthPattern);

        assert validatePattern("254908724112",stringLegthPattern);

        assert !validatePattern("",stringLegthPattern);
    }

    private static boolean validatePattern(String value, String patternStr){

        Pattern pattern = Pattern.compile(patternStr,Pattern.CASE_INSENSITIVE);

        if(value!=null) {
            Matcher m = pattern.matcher(value);
            if (m.find()) {
                return true;
            }
        }

        return false;
    }
}
