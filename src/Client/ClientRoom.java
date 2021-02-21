package Client;

import Server.SetFontFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client端聊天主界面
 */
public class ClientRoom extends JFrame implements ActionListener {

    //连接
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ClientReceive clientReceive;
    private boolean flagClient = false;//判断是否登录过
    //北边工具
    private JPanel NorthPanel;
    private JLabel userNameLabel;
    private JTextField userNameField;
    private JButton loginButton; //登录按钮
    private JButton logoutButton; //登出按钮
    private JButton exitButton; //退出按钮
    private JButton setFontButton;//字体设置按钮
    //中间
    private JScrollPane clientScrollpane;
    private JTextArea clientArea;
    //底边
    private JPanel southPanel;
    private JLabel sendLabel;
    private JTextField sendField; //发送信息的编辑栏
    private JButton sendButton;//发送按钮

    public ClientRoom(String userName){
        init(userName);
        this.setTitle("简易聊天 Client");
        this.setSize(new Dimension(600,500));
        this.setResizable(false);
        this.setVisible(true);
        this.setLocationRelativeTo(null);     //在屏幕中居中显示
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 初始化Client端聊天界面的组件
     * @param userName 用户名
     */
    public void init(String userName){
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        //北边工具
        NorthPanel = new JPanel();
        NorthPanel.setLayout(new FlowLayout());
        userNameLabel = new JLabel("UserName：");
        userNameField = new JTextField(7); //设置用户名
        userNameField.setEnabled(false);
        userNameField.setText(userName);//设置当前用户的用户名
        loginButton = new JButton("Login");
        logoutButton = new JButton("Logout");
        setFontButton = new JButton("Set Font");
        //监听器
        loginButton.addActionListener(this);
        logoutButton.addActionListener(this);
        setFontButton.addActionListener(this);
        logoutButton.setEnabled(false);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        NorthPanel.add(userNameLabel);//用户名
        NorthPanel.add(userNameField);
        NorthPanel.add(loginButton);
        NorthPanel.add(logoutButton);
        NorthPanel.add(setFontButton);
        NorthPanel.add(exitButton);
        NorthPanel.setVisible(true);
        container.add(NorthPanel, BorderLayout.NORTH);

        //中间
        clientArea = new JTextArea();
        clientArea.setFont(new Font("宋体",Font.BOLD,16));
        clientArea.setEnabled(false);
        clientArea.setVisible(true);
        clientScrollpane = new JScrollPane(clientArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.add(clientScrollpane, BorderLayout.CENTER);

        //底边
        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        sendLabel = new JLabel("Send Message：");
        sendField = new JTextField(25);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        southPanel.add(sendLabel);
        southPanel.add(sendField);
        southPanel.add(sendButton);
        sendField.setEnabled(false);
        sendButton.setEnabled(false);
        container.add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * 连接Server并登录
     */
    public void loginFunc(){
        //建立连接
        try {
            socket = new Socket("127.0.0.1", 8888);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"服务器未开启",
                    "Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        flagClient = true;
        //输入输出流
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            oos.writeObject(userNameField.getText());
            ois = new ObjectInputStream(socket.getInputStream());

            clientReceive = new ClientReceive(userNameField,clientArea, socket, oos, ois);
            new Thread(clientReceive).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendField.setEnabled(true);
        sendButton.setEnabled(true);
        loginButton.setEnabled(false);
        logoutButton.setEnabled(true);
        userNameField.setEnabled(false);
        clientArea.append(userNameField.getText()+"上线成功！\n");
    }

    /**
     * 登出并与Server端断开连接
     */
    public void logoutFunc(){
        try {
            oos.writeObject("下线");
            oos.flush();
            oos.close();
            ois.close();
            socket.close();
            clientArea.append("已下线！\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginButton.setEnabled(true);
        logoutButton.setEnabled(false);
        sendField.setEnabled(false);
        sendButton.setEnabled(false);
        userNameField.setEnabled(true);
    }

    /**
     * Client端发送信息
     */
    public void sendFunc(){
        String msgField = sendField.getText();
        String uName = "Server";
        if(msgField.indexOf("@")==0){
            String[] tempS = msgField.split(":");
            uName = tempS[0].substring(1,tempS[0].length());
            clientArea.append("你"+tempS[0]+":"+tempS[1]+"\n");
        }else{
            clientArea.append(userNameField.getText()+"："+msgField+"\n");
        }
        try {
                oos.writeObject("聊天");
                oos.flush();
                oos.writeObject(uName);
                oos.flush();
                oos.writeObject(userNameField.getText());
                oos.flush();
                oos.writeObject(msgField);
                oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendField.setText("");
    }

    /**
     * Client端退出界面并关闭界面
     */
    public void exitFunc(){
        int flag = JOptionPane.showConfirmDialog(this,"是否退出？","Exit",
                JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(flag == JOptionPane.YES_OPTION){
            if(flagClient == true){
                try {
                    if(!socket.isClosed()){
                        oos.close();
                        ois.close();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if(source == loginButton){
            this.loginFunc();
        }else if(source == logoutButton){
            this.logoutFunc();
        }else if(source == sendButton){
            this.sendFunc();
        }else if(source == exitButton){
            this.exitFunc();
        } else if (source == setFontButton) {
            new SetFontFrame(clientArea);
        }
    }
}
