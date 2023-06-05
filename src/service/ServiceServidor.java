package service;

import bean.ChatMessage;
import bean.ChatMessage.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
            System.out.println("Servidor ativo!");
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
                        boolean isConnect= connect(mensagem, output);
                        if (isConnect){
                            listandoOnline.put(mensagem.getNome(), output);
                            sendOnlines();
                        }
                    } 
                    else if(action.equals(Action.DESCONECTADO)){
                        disconnect(mensagem, output);
                        sendOnlines();
                        
                        return;
                    }
                    else if(action.equals(Action.SEND_ONE)) { // envio de mensagens reservadas
                        sendOne(mensagem);
                        }
                    else if (action.equals(Action.SEND_ALL)){
                        sendAll(mensagem);
                        }
                }
            }
            catch (IOException ex) {
		ChatMessage message = new ChatMessage();
		message.setNome(mensagem.getNome());
		disconnect(message, output);
		sendOnlines();
		System.out.println(message.getNome() + " deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

    }
    private boolean connect(ChatMessage message, ObjectOutputStream output){
        if (listandoOnline.size() == 0) {
        message.setTexto("YES");
        send(message, output);
        return true;
    }

    if (listandoOnline.containsKey(message.getNome())) { 
        message.setTexto("NO");
        send(message, output);
        return false;
    } else {
        message.setTexto("YES");
        send(message, output);
        return true;
    } 

//        sendOne(message);
//         regra que evita que tenha 2 clientes com o mesmo nome
//        if(listandoOnline.isEmpty()){ // Caso inicie a lista
//            message.setTexto("Você se conectou!");
//            sendOne(message);
//            return true;
//        }
//        for (Map.Entry<String,ObjectOutputStream> kv : listandoOnline.entrySet()){  // Percorrendo a lista e testando se o nome que vier agora é dferente do que ja tem na lista
//            if (kv.getKey().equalsIgnoreCase(message.getNome())){
//                message.setNome("NO");
//                send(message,output);
//                return false; // a conexão não vai acontecer
//            }
//            else{
//                message.setTexto("Conexão aceita");
//                send(message,output);
//                return true; // quando o nome da chave for diferente de qualquer um existente
//            }
//        }
//        return false;
    }
     private void disconnect(ChatMessage mensagem, ObjectOutputStream output) {
            listandoOnline.remove(mensagem.getNome()); // remover usuário da nossa lista
            
            mensagem.setTexto("Até logo!");
            
            // Mandando mensagem para todos quando desconectar
            mensagem.setAcao(Action.SEND_ONE);
            
            sendAll(mensagem);
            
           System.out.println("User " + mensagem.getNome()+ " saiu da sala" );
           
                    
        }
    
    private void send(ChatMessage message, ObjectOutputStream output){
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void sendOne(ChatMessage message){
         for (Map.Entry<String, ObjectOutputStream> kv : listandoOnline.entrySet()){
             if(kv.getKey().equals(message.getReservado_nome())){
        try {
            kv.getValue().writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
  }
}
    private void sendAll(ChatMessage mensagem) {
       // Envia mensagem para todos os clientes
       for (Map.Entry<String, ObjectOutputStream> kv : listandoOnline.entrySet()){
           // se a chave da posição atual do for for diferente do cliente que está mandando a mensagem
           // essa mensagem é enviada para o cliente que possui essa chave, evita que essa mensagem do cliente seja enviada para ele mesmo
           if (!kv.getKey().equals(mensagem.getNome())){
               mensagem.setAcao(Action.SEND_ONE);
               try {
                   kv.getValue().writeObject(mensagem);
                   // o value é o output
                   // o kv contem o nome do cliente
               } catch (IOException ex) {
                   Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
               }
       }
       }
    }
    private void sendOnlines(){
        Set<String> setNames=  new HashSet<String>();
         for (Map.Entry<String, ObjectOutputStream> kv : listandoOnline.entrySet()){
             setNames.add(kv.getKey());
         }
        
        ChatMessage message = new ChatMessage();
        message.setAcao(Action.USUARIOS_ONLINE);
        message.setSetOnlines(setNames);
        // mandar esse set dos nomes das listas dos onlines como parametro
        // Envia mensagem para todos os clientes
       for (Map.Entry<String, ObjectOutputStream> kv : listandoOnline.entrySet()){
           message.setNome(kv.getKey());
           try {
                   
                   kv.getValue().writeObject(message);
                   // o value é o output
                   // o kv contem o nome do cliente
               } catch (IOException ex) {
                   Logger.getLogger(ServiceServidor.class.getName()).log(Level.SEVERE, null, ex);
               }
       }
       }
    
    
    
}

