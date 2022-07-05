import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.*;  
import java.net.*;
import java.util.*;
import java.util.Date;

public class JavaUdpServer {
	private enum State {
		SOURCE,
		NODE,
		TARGET
	}
	
	private static String word = "Hello World";
	private static int serverport = 5011;
	private static State myState = null;
	private static DatagramSocket socket = null;
	private static String message = null;
	private static SocketAddress sockaddr = null;
	
	private static List<String> addressTarget = new ArrayList<String>();
	

    public static void main(String[] args) throws IOException {
		try {
			socket = new DatagramSocket(serverport);
		} catch (IOException ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		
		addressTarget.add("192.168.210.180");
		addressTarget.add("192.168.210.196");
		addressTarget.add("192.168.210.152");
		addressTarget.add("192.168.210.197");
		addressTarget.add("192.168.210.174");
		
		Scanner sc = new Scanner(System.in); //System.in is a standard input stream  
		System.out.println("Choose the state of the node: ");
		System.out.println("(1) as the Source Node!");
		System.out.println("(2) as the Normal Node!");
		System.out.println("(3) as the Target Node!");
		while (myState == null) {
			String in = sc.nextLine();              //reads string   
			if (in.equals("1") || in.equals("2") ||in.equals("3")) {
				if (in.equals("1")) {
					myState = State.SOURCE;
				} else if (in.equals("2")) {
					myState = State.NODE;
				} else {
					myState = State.TARGET;
				}
			}
		}
		
		node();
    }
	
	private static void node() {
		while (true) {
			if (myState.equals(State.SOURCE)) {
				message = word;
				sendToDiffPCs();
				sockaddr = null;
				myState = State.NODE;
			} else {
				Boolean isSharing = (message == null);
				waitingMessage();
				if (isSharing && myState.equals(State.NODE)) {
					try {
						Thread.sleep(1000);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					
					sendToDiffPCs();
				}
			}
		}
	}
	
	private static void sendToDiffPCs() {
		for (String addresse : addressTarget) {
			try {
				if(!InetAddress.getLocalHost().getHostAddress().equals(addresse)) {
					sendMessage(new InetSocketAddress(InetAddress.getByName(addresse), serverport));
				}
			} catch (UnknownHostException ex) {
			System.out.println(ex.toString());
		}
		}
	}
	
	private static void waitingMessage() {
		try {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			
			SocketAddress incomingAddr = new InetSocketAddress(packet.getAddress(), packet.getPort());
			
			if (message == null) {
				printMessage(true, new String(packet.getData(), 0, packet.getLength()));
				sockaddr = incomingAddr;
			} else {
				System.out.println("Get new request from: (Not gonna forwarding again!)");
				printMessage(false, new String(packet.getData(), 0, packet.getLength()));
				System.out.println("Request from: " + ((InetSocketAddress)incomingAddr).getAddress().toString().split("/")[1] + ":" + ((InetSocketAddress)incomingAddr).getPort());
				System.out.println("--------------------------------------------------------");
			}
			
			if (sockaddr != null) {
				printMessage(false, message);
				System.out.println("Init request: " + ((InetSocketAddress)sockaddr).getAddress().toString().split("/")[1] + ":" + ((InetSocketAddress)sockaddr).getPort());
			} else {
				System.out.println("This is Source Node!");
			}
			
			System.out.println("******************************************************************");
			
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
	}
	
	private static void printMessage(Boolean overRideMessage, String msg) {
		String[] splitMessage = msg.split("SPLITTER");
		if (overRideMessage)
			message = splitMessage[0];
		System.out.println("Message: " + splitMessage[0]);
		if (splitMessage.length > 1) {
			long latency = (new Date().getTime() - Long.valueOf(splitMessage[1]));
			if (latency < 0)
				latency = 0 - latency;
			System.out.println("Latency: " + latency + " ms");
		}
	}
	
	private static void sendMessage(SocketAddress addr) {
		try {
			byte[] buf = (message + "SPLITTER" + new Date().getTime()).getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, addr);
			socket.send(packet);
			
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
	}    
}
