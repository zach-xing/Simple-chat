package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录用户或者注册用户的界面
 */
public class Register extends JFrame implements ActionListener {

    private JPanel panel = new JPanel();
    private JLabel userLabel = new JLabel("User:");     // 创建UserJLabel
    private JTextField userText = new JTextField();           // 获取登录名
    private JLabel passLabel = new JLabel("Password:");       // 创建PassJLabel
    private JPasswordField passText = new JPasswordField(20); //密码框隐藏
    private JButton loginButton = new JButton("login");       // 创建登录按钮
    private JButton registerButton = new JButton("register"); // 创建注册按钮
    private JDBCUtils jdbcUtils = new JDBCUtils();

    public Register() {
        //设置窗体的位置及大小
        this.setTitle("Welcom");
        this.setSize(300, 200);
        this.setLocationRelativeTo(null);     //在屏幕中居中显示
        this.add(panel);                                      // 添加面板
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置X号后关闭
        panel.setLayout(null);  //设置布局为 null

        // 创建 UserJLabel
        userLabel.setBounds(30, 30, 80, 25);
        panel.add(userLabel);
        // 创建文本域用于用户输入
        userText.setBounds(105, 30, 165, 25);
        panel.add(userText);

        // 创建PassJLabel
        passLabel.setBounds(30, 60, 80, 25);
        panel.add(passLabel);
        // 密码输入框 隐藏
        passText.setBounds(105, 60, 165, 25);
        panel.add(passText);

        // 创建登录按钮
        loginButton.setBounds(25, 100, 80, 25);
        panel.add(loginButton);
        loginButton.addActionListener(this);
        registerButton.setBounds(190, 100, 80, 25);
        panel.add(registerButton);
        registerButton.addActionListener(this);
        this.setVisible(true); //设置窗体可见
    }


    public static void main(String[] args) {
        new Register();
    }

    /**
     * 用户登录，查询数据库是否有该数据
     */
    public void loginFunc(){
        String userName = userText.getText();
        String passWord = new String(passText.getPassword());
        if (jdbcUtils.findUser(userName, passWord) == true) {
            //关闭当前界面
            this.dispose();
            //打开Client界面
            new ClientRoom(userName);
        }else{
            JOptionPane.showMessageDialog(this,"登录失败！",
                    "Fail",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 注册用户，添加到数据库中
     */
    public void registerFunc(){
        String userName = userText.getText();
        String passWord = new String(passText.getPassword());
        if (jdbcUtils.findUser(userName, passWord) == true) {
            JOptionPane.showMessageDialog(this,"注册失败！用户已存在",
                    "Fail",JOptionPane.ERROR_MESSAGE);
        }else{
            jdbcUtils.addUser(userName,passWord);
            JOptionPane.showMessageDialog(this,"注册成功！",
                    "Fail",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if(source == loginButton){
            this.loginFunc();
        }else if(source == registerButton){
            this.registerFunc();
        }
    }
}
