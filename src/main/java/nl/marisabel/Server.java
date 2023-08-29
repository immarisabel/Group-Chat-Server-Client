package nl.marisabel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// listens to clients to connect and create new treads when they do
public class Server {

 public static void main(String[] args) throws IOException {

  ServerSocket serverSocket = new ServerSocket(1234);
  Server server = new Server(serverSocket);
  server.startServer();
 }


 // listen for incomming connections and creating the socket to communicate with them
 private ServerSocket serverSocket;

 public Server(ServerSocket serverSocket) {
  this.serverSocket = serverSocket;
 }

 //keep server running
 public void startServer() {
  try {

   //run server socket until we close it
   while (!serverSocket.isClosed()) {

    Socket socket = serverSocket.accept();
    System.out.println("New client has connected.");
    ClientHandler clientHandler = new ClientHandler(socket);

    Thread thread = new Thread(clientHandler);
    thread.start();
   }

  } catch (IOException e) {
  }
 }


 public void closeServerSocket() {
  try {
   if (serverSocket != null) {
    serverSocket.close();
   }
  } catch (IOException e) {
   e.printStackTrace();
  }


 }


}

