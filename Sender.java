import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.io.IOException;

public class Sender {
	static DatagramSocket senderSocket;
	static InetAddress receiverAddress;
	static int receiverPort = 5000;
	static LinkedList<Packet> packetList = new LinkedList<Packet>();
	static LinkedList<Integer> ackList = new LinkedList<Integer>();
	static int sequenceFirst = 0, sequenceN = 0, ackReceived = 0;
	static int count = 0;
	
	public static void main(String[] args) {
		try {
			senderSocket = new DatagramSocket();
			receiverAddress = InetAddress.getByName("127.0.0.1");
			while (true) {
				while (sequenceN-sequenceFirst<NetworkConstants.WINDOWSIZE && sequenceN!=NetworkConstants.TOTALFRAMES) {
					count = 0;
					byte[] packetData = getAlphaNumericString(6).getBytes();
					Packet packet = new Packet(sequenceN, packetData);
					sendDatagramPacket(packet, false);
					ackList.add(sequenceN);
					sequenceN++;
					if (senderSocket.getSoTimeout() == NetworkConstants.RESET) {
						senderSocket.setSoTimeout(NetworkConstants.TIMER);
					}
				}
				try {
					do {
						byte[] ackBytes = new byte[NetworkConstants.ACKFRAMESIZE];
						DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);
						senderSocket.receive(ackPacket);
						Acknowledgement finalAckPacket = (Acknowledgement) Serializer.deserialize(ackPacket.getData());
						ackReceived = finalAckPacket.getAcknowledgementNumber();
						System.out.println("\033[1;34m" + "ACK Received for Frame: " + ((ackReceived-1) % NetworkConstants.TOTALSEQUENCENUMBERS) + "\033[0m");
						if(ackReceived == NetworkConstants.TOTALFRAMES) {
							System.out.println("TRANSMISSION COMPLETE");
							Packet.printPackets(packetList);
							senderSocket.close();
							return;
						}
						if (ackReceived > sequenceFirst && ackReceived <= sequenceN) {
							sequenceFirst = ackReceived;
						}
					} while(ackList.contains(ackReceived));
				} catch (SocketTimeoutException socketTimeoutException) {
					System.out.println("\033[0;33m" + "Retransmission Triggered" + "\033[0m");
					count++;
					if(count == 4) {
						senderSocket.close();
						System.out.println("Retransmission Triggered Many Times, Transmission Stopped!");
						return;
					}
					int temp = sequenceFirst;
					while(temp < sequenceN) {
						Packet packet = packetList.get(temp);
						sendDatagramPacket(packet, true);
						temp++;
					}
				} catch (ClassNotFoundException classNotFoundException) {
					classNotFoundException.printStackTrace();
				}
			}
		} catch (UnknownHostException unknownHostException) {
			unknownHostException.printStackTrace();
		} catch (SocketException socketException) {
			socketException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	public static void sendDatagramPacket(Packet packet, boolean resentFlag) throws IOException {
		byte[] finalPacket = Serializer.serialize(packet);
		DatagramPacket datagramPacket = new DatagramPacket(finalPacket, finalPacket.length, receiverAddress, receiverPort);
		if (!resentFlag) {
			packetList.add(packet);
		}
		if (Math.random() > NetworkConstants.PROBABILITYOFLOSS) {
			if (!resentFlag) {
				System.out.println("Sent Frame " + (packet.getSequenceNumber() % NetworkConstants.TOTALSEQUENCENUMBERS));
			} else {
				System.out.println("\033[0;36m" + "Resent Frame " + (packet.getSequenceNumber() % NetworkConstants.TOTALSEQUENCENUMBERS) + "\033[0m");
			}
			senderSocket.send(datagramPacket);
		} else {
			if (!resentFlag) {
				System.out.println("\033[0;31m" + "Lost Frame " + (packet.getSequenceNumber() % NetworkConstants.TOTALSEQUENCENUMBERS) + " while sending" + "\033[0m");
			} else {
				System.out.println("\033[0;31m" + "Lost Frame " + (packet.getSequenceNumber() % NetworkConstants.TOTALSEQUENCENUMBERS) + " while resending" + "\033[0m");
			}
		}
	}

	public static String getAlphaNumericString(int n) {
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder stringBuilder = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			int index = (int) (alphaNumericString.length() * Math.random());
			stringBuilder.append(alphaNumericString.charAt(index));
		}
		return stringBuilder.toString();
	}
}