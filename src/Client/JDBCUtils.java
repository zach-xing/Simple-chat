package Client;



import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

/**
 * 用户存储在数据库中
 */
public class JDBCUtils {
    /**
     * 获取数据库的连接
     * @return 数据库的连接
     */
    public Connection getConnection(){
    	InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros = new Properties();
        
    	Connection conn = null;
        try {
        	pros.load(is);
        	
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");
            
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url,user,password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return conn;
    }

    /**
     * 在数据库中查找用户
     * @param name 用户名
     * @param password 用户密码
     * @return 是否找到用户
     */
    public boolean findUser(String name,String password){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        String sql = "select upassword from user where uname = ?";
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,name);
            rs = ps.executeQuery();
            while(rs.next()){
                String pw = rs.getString("upassword");
                if(pw.equals(password)){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加用户到数据库中
     * @param name 用户名
     * @param password 用户密码
     */
    public void addUser(String name,String password){
        Connection conn = getConnection();
        PreparedStatement ps =null;
        String sql = "insert into user value (?,?)";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,name);
            ps.setString(2,password);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String createPassword(int length) {
        String codes = "0123456789" + "ABCDEFGHIJKLNMOPQRSTUVWXYZ" + "abcdefghijklnopqrstuvwxyz";
        StringBuilder id = new StringBuilder();
        // 创建随机函数对象
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char rand = codes.charAt(random.nextInt(codes.length() - 1));
            id.append(rand);
        }
        return id.toString();
    }

    private static String createPassword1(int length) {
        String codes = "ABCDEFGHIJKLNMOPQRSTUVWXYZ" + "abcdefghijklnopqrstuvwxyz";
        StringBuilder id = new StringBuilder();
        // 创建随机函数对象
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char rand = codes.charAt(random.nextInt(codes.length() - 1));
            id.append(rand);
        }
        return id.toString();
    }
}
