package Server;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

/**
 * Server端监听提示信息，并进行一些响应的操作
 */
public class ServerListen implements Runnable{

    private User user;
    private UserList userList;
    private ServerSocket serverSocket;
    private JTextField SListField;
    private JTextArea SLisArea;
    private ServerReceive serverReceive;//接收
    boolean isStop;
    int userCount;
    JLabel userCountLabel;

    public ServerListen(int userCount,JLabel userCountLabel,UserList userList, ServerSocket serverSocket, JTextField SListField, JTextArea SLisArea) {
        this.userList = userList;
        this.serverSocket = serverSocket;
        this.SListField = SListField;
        this.SLisArea = SLisArea;
        this.isStop = true;
        this.userCount = userCount;
        this.userCountLabel = userCountLabel;
    }

    @Override
    public void run() {
        while(this.isStop &&!serverSocket.isClosed()){
            try {
                user = new User();
                //接收客户端连接
                user.socket = serverSocket.accept();
                user.oos = new ObjectOutputStream(user.socket.getOutputStream());
                user.ois = new ObjectInputStream(user.socket.getInputStream());
                String userName = (String)user.ois.readObject();
                user.oos.writeObject("服务启动");
                user.oos.flush();
                user.name = userName; //得到用户的名字
                SLisArea.append("用户 "+userName+" 上线."+"\n");
                userList.addUser(user);//添加在线用户人数
                userCount++;
                userCountLabel.setText("当前在线人数："+userCount);

                //启动接收线程
                serverReceive = new ServerReceive(userCount,userCountLabel,SLisArea, user, userList);
                new Thread(serverReceive).start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
