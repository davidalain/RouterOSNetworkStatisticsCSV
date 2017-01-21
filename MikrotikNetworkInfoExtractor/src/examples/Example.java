package examples;

import javax.net.SocketFactory;

import br.com.davidalain.common.InputKeyboardUtil;
import me.legrange.mikrotik.ApiConnection;

/**
 *
 * @author gideon
 */
 public abstract class Example {
     
    protected void connect() throws Exception {
    	System.out.println("Default target host is " + InputKeyboardUtil.DEFAULT_HOST);
    	System.out.println();
    	
        con = ApiConnection.connect(SocketFactory.getDefault(), InputKeyboardUtil.getHost(), ApiConnection.DEFAULT_PORT, 2000);
        con.login(InputKeyboardUtil.getUserName(), InputKeyboardUtil.getPassword());
        System.out.println("Connected!");
    }

    protected void disconnect() throws Exception {
        con.close();
    }
    
    protected ApiConnection con;
    
}
