import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

	public static byte[] serialize(Object objectToSerialize) throws IOException {
		ByteArrayOutputStream outputStreamObject = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStreamObject);
		objectOutputStream.writeObject(objectToSerialize);
		return outputStreamObject.toByteArray();
	}

	public static Object deserialize(byte[] bytesToDeserialize) throws IOException, ClassNotFoundException {
		ByteArrayInputStream inputSreamObject = new ByteArrayInputStream(bytesToDeserialize);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputSreamObject);
		return objectInputStream.readObject();
	}
}
