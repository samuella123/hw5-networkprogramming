package hw5.hangman.net;

import java.io.*;
import java.net.*;

import hw5.hangman.SafeOutput;
import hw5.hangman.common.*;

/**
 * The ServerConnection class is responsible for communicating the server. Given the ip and port
 * to the <code>connect</code> method, it trys to get connected and then prints the result with
 * <code>client.view.SafeOutput</code> given from the controller. After establishing a connection,
 * We create a <code>client.net.ServerConnection.Listener</code> class on a separete thread in order
 * to take care of the responses of the server and printing them using <code>client.view.SafeOutput</code>.
 * there are 2 staic parameters that should be initialised, <code>TIMEOUT_TIME_HOUR</code> and
 * <code>TIMEOUT_TIME_MIN</code>.
 */

public class ServerConnection
{
    private static int TIMEOUT_TIME_HOUR = 1500000; //the time that the connection can be open whithout any message being transferred.
    private static int TIMEOUT_TIME_MIN = 30000; //the time that we wait for the server to respond to our connection request.

    private volatile Socket serverSocket;
    private volatile PrintWriter toServer;
    private volatile BufferedReader fromServer;
    private volatile Boolean isConnected = false;

    private volatile String ip;
    private volatile int port;
    private SafeOutput safeOut;

    /**
     * It is used by other classes to check whether we are connected to server
     * or not.
     * @return A Bloolean.
     */
    public Boolean getConnected()
    {
      return isConnected;
    }

    public void setNewSafeOutput(SafeOutput newSafe) throws Exception
    {
        safeOut = newSafe;

        disconnect();

        connect(ip, this.port,safeOut);
    }

    /**
     * Creates a new class and connects to the server. Also starts a listener thread for
     * receiving messages from server and printing them.
     *
     * @param host IP address of the server.
     * @param port Server's port number.
     */
    public void connect(String host, int port,SafeOutput safeOut) throws Exception
    {
        this.safeOut = safeOut;
        ip = host;
        this.port = port;
        try
        {
          this.serverSocket = new Socket();
          serverSocket.connect(new InetSocketAddress(host, port), TIMEOUT_TIME_MIN);
          serverSocket.setSoTimeout(TIMEOUT_TIME_HOUR);

          this.isConnected = true;
          Boolean flush = true;

          toServer = new PrintWriter(serverSocket.getOutputStream(), flush);
          fromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        }
        catch(IOException ex)
        {
            throw new Exception("Unable to connect to the server.");
            //throw new Exception(ex.getMessage());
        }

        new Thread(new Listener(this.safeOut)).start();
    }

    /**
     * Sends the message to the server.
     *
     * @param inp The message wants to be sent.
     */
    public void sendMessage(String inp)
    {
        toServer.println(inp);
    }


    /**
     * It will closes the socket and our state changes to not connected.
     */
    public void disconnect() throws Exception
    {
        try
        {
          toServer.println("DISCONNECT");
          serverSocket.close();
        }
        catch(IOException ex)
        {
          throw new Exception("Unable to close the socket.");
        }
        serverSocket = null;
        isConnected = false;
    }


    /**
    * We create This class on a separete thread in order to take care of the
    * responses of the server and printing them using <code>client.view.SafeOutput</code>.
    */
    private class Listener implements Runnable
    {
        private final SafeOutput safeOut;

        private Listener(SafeOutput safeOut)
        {
            this.safeOut = safeOut;
        }

        /**
        * read line from the ongoing connection and observe the
        * responses of the server and printing them using <code>client.view.SafeOutput</code>.
        */
        @Override
        public void run()
        {
            try
            {
                while(true)
                {
                    if(safeOut!=null)
                        reviseSend(fromServer.readLine());
                }
            }
            catch (Exception ex)
            {
                if(safeOut!=null)
                    safeOut.terminatedConnection();
            }
        }

        /**
        * This method trims the message comming from the server and prints it.
        * it splits the message <code>Constants.MSG_DELIMETER</code> and shows it.
        */
        private void reviseSend(String inp)
        {
          String[] msgParts = inp.split(Constants.MSG_DELIMETER);
          if(msgParts.length == 1) //something else
              safeOut.printResult(inp);
          else if(!msgParts[0].equals(MsgType.RESULT.toString())) //welcome or error message
          {
              safeOut.statusMessage(msgParts[1]);
          }
          else
          {
              String[] msgParts2 = msgParts[1].split("\\s+"); //result message
              safeOut.resultMessage(msgParts2[0],msgParts2[1],msgParts2[2]);
              //return msgParts[0] + ": " + msgParts2[0] + " attempts remaining: " + msgParts2[1] + " score: " + msgParts2[2];
          }
        }
    }
}
