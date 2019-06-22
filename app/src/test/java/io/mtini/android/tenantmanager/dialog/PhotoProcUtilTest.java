package io.mtini.android.tenantmanager.dialog;

import com.google.common.base.Charsets;
import com.prelimtek.android.picha.view.PhotoProcUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class PhotoProcUtilTest {

    String message = "Welcome to Tutorials;"
            +"Welcome to Tutorials; \n"
            +"Welcome to Tutorials;"
            +"Welcome to Tutorials; \n"
            +"Welcome to Tutorials;"
            +"Welcome to Tutorials; \n"
            +"Welcome to Tutorials;"
            +"Welcome to Tutorials; \n"
            +"Welcome to Tutorials;";

    @Before
    public void init(){

    }
    @Test
    public void testCompressionDecompression() throws DataFormatException {
        PhotoProcUtil.CompressionCodec codec = new PhotoProcUtil.CompressionCodec();

        byte[] input = message.getBytes(Charsets.UTF_8);
        int input_size = input.length;
        System.out.println("input size "+input.length);

        byte[] compressed = codec.compress(input);
        System.out.println("compressed size "+compressed.length);

        byte[] output = codec.decompress(compressed);
        System.out.println("output size "+output.length);
        System.out.println(new String(output));

        assertEquals(input.length,output.length);
    }


}