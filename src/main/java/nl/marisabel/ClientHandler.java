package nl.marisabel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

 // collect the clients opened to communicate to. Static because it belongs to the class, not the objects.
 public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
 private Socket socket;
 private BufferedReader bufferedReader;
 private BufferedWriter bufferedWriter;
 private String clientUsername;

 public ClientHandler(Socket socket) {

  try {
   this.socket = socket;

   // we need characters, not bites, hence OutputStream
   this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

   this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

   this.clientUsername = bufferedReader.readLine();

   clientHandlers.add(this);

   broadcastMessage("SERVER: " + clientUsername + " has entered the chat.");
  } catch (IOException e) {
   closeEverything(socket, bufferedReader, bufferedWriter);
  }
 }

 // anything here will be run on a  separate thread to wait for messages and sending
// readline() needs to be aside always running
 @Override
 public void run() {
  String messageFromClient;
  while (socket.isConnected()) {
   try {
    messageFromClient = bufferedReader.readLine();
    broadcastMessage(messageFromClient);
   } catch (IOException e) {
    closeEverything(socket, bufferedReader, bufferedWriter);
    break; // NEEDS TO ALLOW US TO STOP!
   }
  }
 }


 public void broadcastMessage(String messageToSend) {
  for (ClientHandler clientHandler : clientHandlers) {
   try {
    // broadcast to all EXCEPT who sent it
    if (!clientHandler.clientUsername.equals(clientUsername)) {
     clientHandler.bufferedWriter.write(messageToSend);
     clientHandler.bufferedWriter.newLine(); // same as ENTER
     clientHandler.bufferedWriter.flush(); // messages wont be sent until they are full, like movies buffering. But we need to so so manually because messages are small.

    }
   } catch (IOException e) {
    closeEverything(socket, bufferedReader, bufferedWriter);
   }
  }
 }


 public void removeClientHandler() {
  clientHandlers.remove(this);
  broadcastMessage("SERVER: " + clientUsername + " has left the chat.");

 }


 public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
  removeClientHandler();
  try {
   if(bufferedReader!= null){
    bufferedReader.close();
   }
   if (bufferedWriter != null) {
    bufferedWriter.close();
   }
   if (socket != null){
    socket.close();
   }

   } catch (IOException e){
   e.printStackTrace();
  }
  }




}
