package Server;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 存储在线的用户
 */
public class UserList {
    private ArrayList<User> users;
    private int userCount=0;

    public UserList(){
        users = new ArrayList<>();
    }

    /**
     * 添加在线用户
     * @param user 用户
     */
    public void addUser(User user){
        users.add(user);
        userCount++;
    }

    /**
     * 根据用户下标找用户
     * @param k 用户下标
     * @return 用户
     */
    public User findUser(int k){
        return users.get(k);
    }

    /**
     * 根据用户名字找到用户
     * @param str 用户名
     * @return 用户
     */
    public User findUser(String str){
        Iterator it = users.iterator();
        while(it.hasNext()){
            User temp = (User) it.next();
            if (str.equals(temp.name)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * 得到在线用户的个数
     * @return 个数
     */
    public int getuserCount(){
        return this.userCount;
    }

    /**
     * 删除在线用户
     * @param name 用户名
     */
    public void deleteUser(String name){
        Iterator it = users.iterator();
        while(it.hasNext()){
            User temp = (User) it.next();
            if (name.equals(temp.name)) {
                it.remove();
                userCount--;
                return;
            }
        }
    }
}
