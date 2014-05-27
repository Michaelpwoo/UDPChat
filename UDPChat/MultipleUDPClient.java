/* 
 Michael Woo
 4/14/14
*/
import java.io.*;  
import java.net.*;

class UDPClientSendThread extends Thread {
 
    private InetAddress address;
    private int port;
    private DatagramSocket socket;

    //constructor
    public UDPClientSendThread(InetAddress address, int port) throws SocketException {
        this.socket = new DatagramSocket();
        this.port = port;
        this.address = address;
        socket.connect(address, port);
    }

    public DatagramSocket getSocket() {
        return socket;
    }
    
    //thread
    public void run() {       
        try {        
            byte [] infoData = new byte[1024];
            String data = "Client Connecting...: " + address + ":" + port;
            infoData = data.getBytes();
            DatagramPacket infoPacket = new DatagramPacket(infoData, infoData.length, address, port);
            socket.send(infoPacket);
 
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) 
            {
 
                //message to send
                String message = br.readLine();

                //create buffer and get ready to send data
                byte[] sendData = new byte[1024];   
                sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);

                if (message.equals("end_communication")) {
                    System.out.println("Disconnecting from Server. Bye");
                    data = "Client Disconnecting...: " + address + ":" + port;
                    sendData = data.getBytes();
                    DatagramPacket endPacket = new DatagramPacket(sendData, sendData.length, address, port);
                    socket.send(endPacket); 
                    socket.close();
                    System.exit(0);
                } 
                //send data
                socket.send(sendPacket);
            }
        }
        catch (IOException ex) {

        }
    }
}   

class UDPClientReceiverThread implements Runnable {
 
    private DatagramSocket socket;
 
    public UDPClientReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }
 
    public void run() {
 
        byte[] receiveData = new byte[1024];
 
        while (true) {            
            //receiving packet
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            if(socket.isClosed()){
                System.exit(1);
            }
            try {
                
                socket.receive(receivePacket);
                //get message   
                String message =  new String(receivePacket.getData(), 0, receivePacket.getLength());
                // print to the screen
                System.out.println(message);
            } 
            catch (IOException ex) {

            }
        }
    }
}

public class MultipleUDPClient{
    
    
    public static void main(String args[]) throws IOException {
        
        String local = "127.0.0.1";
        if (args.length != 1) {
            throw new IllegalArgumentException("Paramater: <Port>");
        }
        int port = Integer.parseInt(args[0]);
        
        
        System.out.println("Connecting to host: " + local + ", Port:  " + port);
        InetAddress address = InetAddress.getByName(local);
        //init client's threads
        UDPClientSendThread send = new UDPClientSendThread(address, port);
        send.start();
        Thread receive = new Thread( new UDPClientReceiverThread(send.getSocket()));
        receive.start();
    }
}