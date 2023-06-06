package service;

import bean.ChatMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author taina
 */
public class ServiceCliente {
   private Socket socket ;
   private ObjectOutputStream output;
   
   public Socket connect(){
       try {
           this.socket = new Socket("localhost", 1234);
           this.output = new ObjectOutputStream(socket.getOutputStream());
       } catch (IOException ex) {
           Logger.getLogger(ServiceCliente.class.getName()).log(Level.SEVERE, null, ex);
       }
       return socket;
   
}
   public void send (ChatMessage message){
       try {
           output.writeObject(message);
       } catch (IOException ex) {
           Logger.getLogger(ServiceCliente.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
