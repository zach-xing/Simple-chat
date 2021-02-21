package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server端聊天主界面
 */
public class ServerRoom extends JFrame implements ActionListener{

    //连接
    private ServerSocket serverSocket;
    private Socket socket;
    private UserList users;
    private ServerListen serverListen; //监听连接
    private boolean flagServerSocket = false;//判断是否开启过服务

    //北边工具
    public int userCount = 0;
    JLabel usercountLabel;//显示当前在线人数
    private JToolBar serverToolBar = new JToolBar(); //界面上层的工具栏
    private JButton portSetButton; //端口设置
    private JButton startServerButton; //启动按钮
    private JButton stopServerButton; //停止按钮
    private JButton exitButton; //退出按钮
    private JButton setFontButton;//设置字体按钮
    //中间工具
    private JTextArea showMSG;
    private JScrollPane showMSGPane; //带滚动条的面板
    //南边工具
    private JPanel SouthPanel;//南方面板
    private JLabel sendMSGLabel; //发送信息
    private JTextField sendMSGField; //消息输入栏
    private JButton sendButton; //发送按钮

    public ServerRoom(){
        init();
        this.setTitle("聊天室 Server");
        this.setSize(new Dimension(600,500));
        this.setLocationRelativeTo(null);     //在屏幕中居中显示
        this.setResizable(false);//设置不能更改大小
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 初始化Server聊天的界面
     */
    public void init(){
        Container container = this.getContentPane(); //内容面板
        container.setLayout(new BorderLayout()); //设置成边界布局

        //创建按钮以及添加到工具栏中,并一起放在界面的上边
        //北边
        portSetButton = new JButton("Port Set");
        startServerButton = new JButton("Start Server");
        stopServerButton = new JButton("Stop Server");
        exitButton = new JButton("Exit");
        setFontButton = new JButton("Set Font");
        serverToolBar.add(startServerButton);
        serverToolBar.addSeparator();//添加分隔栏
        serverToolBar.add(stopServerButton);
        serverToolBar.addSeparator();//添加分隔栏
        serverToolBar.add(setFontButton);
        serverToolBar.addSeparator();//添加分隔栏
        serverToolBar.add(exitButton);
        stopServerButton.setEnabled(false); //一开始不能停止
        /*给这5个按钮添加动作监听*/
        portSetButton.addActionListener(this);
        startServerButton.addActionListener(this);
        stopServerButton.addActionListener(this);
        exitButton.addActionListener(this);
        setFontButton.addActionListener(this);
        //显示在线人数
        usercountLabel = new JLabel("当前在线人数："+userCount);
        serverToolBar.add(usercountLabel);
        container.add(serverToolBar, BorderLayout.NORTH);

        //中间
        showMSG = new JTextArea();
        showMSG.setEnabled(false); //文本区域不能修改
        showMSG.setFont(new Font("宋体",Font.BOLD,16));
        showMSG.setVisible(true);
        //根据需要显示滚动条
        showMSGPane = new JScrollPane(showMSG,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        showMSGPane.setSize(new Dimension(600, 200));
        container.add(showMSGPane, BorderLayout.CENTER);

        //南边
        SouthPanel = new JPanel();
        SouthPanel.setLayout(new FlowLayout());
        sendMSGLabel = new JLabel("Send Message：");
        SouthPanel.add(sendMSGLabel); //将Label放在Panel中
        sendMSGField = new JTextField(25);
        sendMSGField.setEnabled(false); //一开始不能输入
        SouthPanel.add(sendMSGField); //将Field区域放在Panel中
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false); //未启动服务之前不能使用
        SouthPanel.add(sendButton); //将Send按钮放在Panel中
        container.add(SouthPanel,BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        ServerRoom serverStart = new ServerRoom();
    }

    /**
     * Server端启动服务
     */
    public void StartServer(){
        try {
            serverSocket = new ServerSocket(8888);
            flagServerSocket = true;
            showMSG.append("Server Start,Port is 8888.Listening...\n");
            startServerButton.setEnabled(false);
            sendMSGField.setEnabled(true); //可以输入了
            stopServerButton.setEnabled(true);
            sendButton.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        users = new UserList();//初始化用户列表
        //启动监听线程
        serverListen = new ServerListen(userCount,usercountLabel,users,serverSocket,sendMSGField,showMSG);
        new Thread(serverListen).start();
    }

    /**
     * Server端关闭服务
     */
    public void stopServer(){
        try {
            serverSocket.close();
            if(serverListen.isStop == true) serverListen.isStop = false;
            int count = users.getuserCount();
            for (int i = 0; i < count; i++) {
                User u = users.findUser(i);
                u.oos.writeObject("服务停止");
                u.oos.flush();
                u.oos.close();
                u.ois.close();
                u.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        startServerButton.setEnabled(true);
        sendMSGField.setEnabled(false);
        stopServerButton.setEnabled(false);
        sendButton.setEnabled(false);
        showMSG.append("服务器已关闭！\n");
    }

    /**
     * Server端发送消息
     */
    public void sendServer(){
        String msg = sendMSGField.getText();
        if(msg.indexOf("@")==0){
            String[] tempS = msg.split(":");
            showMSG.append("你"+tempS[0]+":"+tempS[1]+"\n");
        }else{
            showMSG.append("Server："+msg+"\n");
        }
        int count = users.getuserCount();
        for(int i=0;i<count;i++){
            User u = users.findUser(i);
            try {
                u.oos.writeObject("聊天");
                u.oos.flush();
                u.oos.writeObject(u.name);
                u.oos.flush();
                u.oos.writeObject("Server");
                u.oos.flush();
                u.oos.writeObject(msg);
                u.oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //清空输入栏中的数据
        sendMSGField.setText("");
    }

    /**
     * Server端退出主界面
     */
    public void exitServer(){
        int flag = JOptionPane.showConfirmDialog(this,"是否退出？","Exit",
                JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(flag == JOptionPane.YES_OPTION){
            if(flagServerSocket == true){
                try {
                    serverSocket.close();
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
        if(source == startServerButton){ //点击了Start按钮
            this.StartServer();
        }else if(source == stopServerButton){
            this.stopServer();
        }else if(source == sendButton){
            this.sendServer();
        }else if(source == setFontButton){
            new SetFontFrame(showMSG);
        }else if(source == exitButton){
            this.exitServer(); //退出
        }
    }
}
