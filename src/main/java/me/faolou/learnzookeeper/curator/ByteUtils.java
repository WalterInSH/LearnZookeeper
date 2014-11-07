package me.faolou.learnzookeeper.curator;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author sam.yang
 * @since 7/25/14 9:45 AM.
 */
public class ByteUtils {

    public static byte[] toByteArray(Object o) throws IOException {
        if (o == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(o);
        IOUtils.closeQuietly(objectOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null) {
            return null;
        }
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        IOUtils.closeQuietly(inputStream);
        return inputStream.readObject();
    }
}
