package Server;

import javax.swing.*;
import java.io.IOException;

/**
 * Server端中监听接收的信息,并发送信息给Client端
 */
public class ServerReceive implements Runnable{

    private JTextArea textAreaMSG;//聊天的信息
    private User user;
    private UserList userList;
    boolean isStop ;
    int userCount;
    JLabel userCountLabel;

    public ServerReceive(int userCount,JLabel userCountLabel,JTextArea textAreaMSG, User user, UserList userList) {
        this.textAreaMSG = textAreaMSG;
        this.user = user;
        this.userList = userList;
        this.isStop = true;
        this.userCount = userCount;
        this.userCountLabel = userCountLabel;
    }

    /**
     * 给所有用户发送信息
     * @param msg 发送的信息
     */
    public void sendChatMSG(String msg){
        int count = userList.getuserCount();
        for(int i=0;i<count;i++){
            User u = userList.findUser(i);
            try {
                u.oos.writeObject("Server聊天");
                u.oos.flush();
                u.oos.writeObject(user.name);
                u.oos.flush();
                u.oos.writeObject("");
                u.oos.flush();
                u.oos.writeObject(msg);
                u.oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给指定的用户发送信息
     * @param msg 发送的信息
     * @param username1 用户名1
     * @param username2 用户名2
     */
    public void sendChatMSG(String msg,String username1,String username2){
            User u = userList.findUser(username1);
            try {
                u.oos.writeObject("Server聊天");
                u.oos.flush();
                u.oos.writeObject(username1);
                u.oos.flush();
                u.oos.writeObject(username2);
                u.oos.flush();
                u.oos.writeObject(msg);
                u.oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    @Override
    public void run() {
        while (this.isStop && !user.socket.isClosed()) {
            try {
                String t = (String) user.ois.readObject();
                if(t.equals("聊天")){
                    String peopleD = (String) user.ois.readObject();
                    //谁发过来的
                    String peopleS = (String) user.ois.readObject();
                    //发出的聊天信息
                    String message = (String) user.ois.readObject();

                    if(message.indexOf("@")==0){
                        String[] tempS = message.split(":");
                        if(tempS[0].equals("@Server")){
                            textAreaMSG.append(peopleS+" 私聊你："+tempS[1]+"\n");
                        }else{
                            sendChatMSG(message,peopleD,peopleS);
                        }
                    }else{
                        textAreaMSG.append(peopleS+"："+message+"\n"); //追加到文本域的后面
                        sendChatMSG(message);
                    }

                }else if(t.equals("下线")){
                    userList.deleteUser(user.name);
                    String msg = user.name + "下线了.\n";
                    userCount--;
                    userCountLabel.setText("当前在线人数："+userCount);
                    textAreaMSG.append(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
