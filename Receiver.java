import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class Receiver {

	public static void main(String[] args) throws Exception {
		DatagramSocket receiverSocket = new DatagramSocket(5000); 
		byte[] receivedPacketData = new byte[95];
		int waitingForPacketNumber = 0;
		int sequenceNumber = 0;
		LinkedList<Packet> receivedData = new LinkedList<Packet>();
		while(true) {
			DatagramPacket receivedPacket = new DatagramPacket(receivedPacketData, receivedPacketData.length);
			receiverSocket.receive(receivedPacket);
			Packet packet = (Packet) Serializer.deserialize(receivedPacket.getData());
			sequenceNumber = packet.getSequenceNumber();
			System.out.print("\033[1;36m" + "Frame " + (sequenceNumber % NetworkConstants.TOTALSEQUENCENUMBERS) + " received" + "\033[0m");
			if(sequenceNumber == waitingForPacketNumber) {
				System.out.println();
				receivedData.add(packet);
				waitingForPacketNumber++;
				if(waitingForPacketNumber == NetworkConstants.TOTALFRAMES) {
					System.out.println("Sending ACK with Sequence Number: " + (waitingForPacketNumber%NetworkConstants.TOTALSEQUENCENUMBERS));
					Acknowledgement ackFrame = new Acknowledgement(waitingForPacketNumber);
					byte[] ackBytes = Serializer.serialize(ackFrame);
					DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(), receivedPacket.getPort());
					receiverSocket.send(ackPacket);
					receiverSocket.disconnect();
					break;
				}
				Acknowledgement ackFrame = new Acknowledgement(waitingForPacketNumber);
				byte[] ackBytes = Serializer.serialize(ackFrame);
				DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(), receivedPacket.getPort());
				if(Math.random() > NetworkConstants.PROBABILITYOFLOSS) {
					System.out.println("Sending ACK with Sequence Number: " + (waitingForPacketNumber%NetworkConstants.TOTALSEQUENCENUMBERS));
					receiverSocket.send(ackPacket);
				} else {
					System.out.println("\033[0;31m" + "Lost ACK with Sequence Number: " + (waitingForPacketNumber%NetworkConstants.TOTALSEQUENCENUMBERS) + "\033[0m");
				}
			} else {
				System.out.println("\033[1;33m" + " Frame " + (sequenceNumber % NetworkConstants.TOTALSEQUENCENUMBERS) + " discarded (not in order)" + "\033[0m");
			}
		}
		receiverSocket.close();
		System.out.println("RECEPTION COMPLETE");
		Packet.printPackets(receivedData);
	}	
}