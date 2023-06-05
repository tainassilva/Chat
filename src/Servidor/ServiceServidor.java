package Servidor;

import Servidor.ChatMessage.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author taina
 */
public class ServiceServidor {
    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String,ObjectOutputStream>listandoOnline = new HashMap<String,ObjectOutputStream>(); // o objeto que se conectar ao servidor, entra nesta lista
    
    public ServiceServidor(){
        try {
            serverSocket = new ServerSocket(1234);
            
            while(true){ // Manter esse server socket sempre esperando por uma conexão quando o servidor estiver rodando... pode se fazer conexões ilimitadas 
              // Quando o cliente entrar, o accept libera, passando o socket   
              socket = serverSocket.accept();
              
              new Thread(new ListenerSocket(this.socket)).start();
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private class ListenerSocket implements Runnable{

        private ObjectOutputStream output; // executa o envio de mensagens do servidor 
        private ObjectInputStream input; // recevbe as mensagens enviada pelos clientes
        
        public ListenerSocket (Socket socket){
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
         @Override
        public void run(){
            ChatMessage mensagem = null;
            try{
                while ((mensagem = (ChatMessage) input.readObject()) != null){
                    Action action = mensagem.getAcao();
                    
                    if (action.equals(Action.CONECTADO)){
                        connect(mensagem, output);
                    } 
                    else if(action.equals(Action.DESCONECTADO)){
                        
                    }
                    else if(action.equals(Action.SEND_ONE)) { // envio de mensagens reservadas
                        
                        }
                    else if (action.equals(Action.SEND_ALL)){
                        
                        }
                    else if (action.equals(Action.USUARIOS_ONLINE)){       
                        }
                }
            }
            catch (IOException ex) {
                Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        private ListenerSocket() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
    private void connect(ChatMessage message, ObjectOutputStream output){
        sendOne(message,output);
            }
    
    
    private void sendOne(ChatMessage message, ObjectOutputStream output){
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
