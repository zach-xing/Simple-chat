package Client;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client端中监听接收的信息
 */
public class ClientReceive implements Runnable {

    private JTextField nameField;
    private JTextArea receiveArea;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientReceive(JTextField nameField,JTextArea receiveArea, Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        this.nameField = nameField;
        this.receiveArea = receiveArea;
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
    }

    @Override
    public void run() {
        while(!socket.isClosed()){
            try {
                String msg = (String) ois.readObject(); //判断进行什么操作

                if(msg.equals("聊天")){
                    String peopleD = (String) ois.readObject(); //得到用户的名字(发送给的谁)
                    String peopleS = (String) ois.readObject();//谁发过来的
                    String msgChat = (String) ois.readObject();//得到聊天记录
                    if(peopleD.equals(nameField.getText())){
                        if(msgChat.indexOf("@")==0){
                            String[] tempS = msgChat.split(":");
                            if(tempS[0].equals("@"+peopleD)){
                                receiveArea.append(peopleS+" 私聊你："+tempS[1]+"\n");
                            }
                        }else{
                            receiveArea.append(peopleS+"："+msgChat+"\n");
                        }
                    }
                }else if(msg.equals("服务停止")){
                    ois.close();
                    oos.close();
                    socket.close();
                    receiveArea.append("服务器服务已经关闭！\n");
                }else if(msg.equals("服务启动")){
                    receiveArea.append("服务器服务已经启动！\n");
                }else if(msg.equals("Server聊天")){
                    String people = (String) ois.readObject(); 
                    String peopleT = (String) ois.readObject();
                    String msgChat = (String) ois.readObject();//得到聊天记录
                    if(msgChat.indexOf("@")==0){
                        String[] tempS = msgChat.split(":");
                        if(nameField.getText().equals(people)){
                            receiveArea.append(peopleT+" 私聊你："+tempS[1]+"\n");
                        }
                    }else{
                        if(!people.equals(nameField.getText())){
                            receiveArea.append(people+"："+msgChat+"\n");
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
