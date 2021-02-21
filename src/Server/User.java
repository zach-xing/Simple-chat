package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 用户
 */
public class User {
    String name = null;
    Socket socket = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
}
