package br.com.davidalain.main;

import java.util.List;
import java.util.Map;

import examples.Example;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 7: Dump your complete config
 *
 * @author gideon
 */
public class ListFiles extends Example {

    public static void main(String... args) throws Exception {
    	
    	System.out.println("====* List files *====");
    	
        ListFiles ex = new ListFiles();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException {
    	
        List<Map<String, String>> res = con.execute("/file/print");
        
        for (Map<String, String> line : res) {
            System.out.println(line);
        }
    }
}
