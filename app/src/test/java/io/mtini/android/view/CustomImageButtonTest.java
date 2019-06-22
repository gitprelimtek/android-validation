package io.mtini.android.view;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CustomImageButtonTest {

    public static final String regex = "([0-9])+dp";
    public static final Pattern dimensionsPattern = Pattern.compile(regex);

    private int dpToInt(String value){

        Matcher m = dimensionsPattern.matcher(value);
        if (m.find()){
            String s = m.group(0).replaceAll("[^0-9]","");
            System.out.println(s);
            return Integer.parseInt(s);
        }

        //return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,getResources().getDisplayMetrics());
        return -1;
    }

    @Test
    public void test(){

        String dim = "100dp";

        int out = dpToInt(dim);

        assert out==100;

    }


    static String regex2 = "(?!.*/)(.*?)(?=(\\.[^.]*$))";
    static String regex1 = "(.*/)*.+\\.(png|jpg|gif|bmp)";
    public static final Pattern imageNamePattern = Pattern.compile(regex2);
    private String toName(String imagePath){
        Matcher m = imageNamePattern.matcher(imagePath);
        if (m.find()) {
            String s = m.group(0);
            System.out.println(s);
            return s;
        }
        return imagePath;
    }

    @Test
    public void test2(){
        String path = "res/drawable/file.png";

        String out = toName(path);

        System.out.println(out);

        assertEquals("file",out);
    }

}