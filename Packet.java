import java.io.Serializable;
import java.util.LinkedList;

class Packet implements Serializable {
	private static final long serialVersionUID = 2031779179280273514L;
	
	public int sequenceNumber;
	public byte[] packetData;

	Packet(int sequenceNumber, byte[] packetData) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.packetData = packetData;
	}

	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public byte[] getPacketData() {
		return this.packetData;
	}
	
	public static void printPackets(LinkedList<Packet> packets) {
		for(Packet packet: packets) {
			String dataString = new String(packet.getPacketData());
			System.out.print("\033[0;35m" + dataString + "\033[0m");
		}
		System.out.println();
	}
}