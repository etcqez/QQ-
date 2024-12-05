package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class QQServer {

    //http套接字
    private ServerSocket serverSocket = null;
    //创建一个集合，存放多个用户，如果是这些用户登录，就认为是合法的
    //线程安全
    private static ConcurrentHashMap<String, User> validUser = new ConcurrentHashMap<>();

    //模拟数据库，初始化有效的登录用户
    static {
        validUser.put("100", new User("100", "123456"));
        validUser.put("200", new User("200", "123456"));
        validUser.put("300", new User("300", "123456"));
        validUser.put("至尊宝", new User("至尊宝", "123456"));
        validUser.put("紫霞仙子", new User("紫霞仙子", "123456"));
    }

    //验证用户是否有效的方法
    //判断用户名 -> 判断密码
    private boolean checkUser(String userId, String passwd) {

        //通过用户名获取集合对象
        User user = validUser.get(userId);
        //说明userId没有在validUsers的 key 中
        if (user == null) {
            return false;
        }
        //userId正确，但是密码错误
        if (!user.getPasswd().equals(passwd)) {
            return false;
        }
        return true;
    }

    /**
     * 服务器一直监听 ->  发送登录成功消息 -> 保持通信(启动一个连接线程)
     */
    public QQServer() {
        try {
            System.out.println("服务器在9999端口监听");
            serverSocket = new ServerSocket(9999);

            while (true) {
                //一直监听
                Socket socket = serverSocket.accept();
                //将字节输入流转化为对象输入流
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                //通过对象输入流读取 User
                User user = (User) objectInputStream.readObject();
                Message message = new Message();

                //登录成功
                if (checkUser(user.getUserId(), user.getPasswd())) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //回复
                    objectOutputStream.writeObject(message);

                    //创建一个连接线程，保持和客户端通信
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                    serverConnectClientThread.start();
                    //加入连接线程集合
                    ManagerClientThreads.addClientConnectServerThread(user.getUserId(), serverConnectClientThread);

                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + "退出");
                } else {//登录失败
                    System.out.println("用户 id=" + user.getUserId() + " passwd=" + user.getPasswd() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //如果服务器退出了while
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
