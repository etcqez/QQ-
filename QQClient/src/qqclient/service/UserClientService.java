package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户注册和用户登录验证及用户的操作
 */
public class UserClientService {

    private User user = new User();
    private Socket socket;

    /**
     * 根据UserId 和 pwd 到服务器验证该用户是否合法
     * 发送user对象 -> 接收 message -> 判断MessageType
     * @param userId
     * @param pwd
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public  boolean checkUser(String userId, String pwd) throws IOException, ClassNotFoundException {
        boolean b = false;

        //创建User对象
        user.setUserId(userId);
        user.setPasswd(pwd);

        //连接到服务端，验证User对象
        socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
        //创建对象输出流 发送User对象
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(user);

        //读取从服务器回复的Message对象
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message message = (Message) objectInputStream.readObject();

        //验证对象的 MesType
        if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {

            //创建一个和服务器端保持通信的线程
            ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
            clientConnectServerThread.start();

            //将通信线程放入到集合中管理
            ManagerClientConnectServerThread.addClientConnectServerThread(userId,clientConnectServerThread);

            b = true;
        } else {
            socket.close();
        }
        return b;
    }

    //向服务器端请求在线用户列表
    //获取通信线程(从 管理客户端连接服务器线程) -> 发送message
    public void onlineFrientList() {

        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserId());
        try {
            //根据用户名获取用户通信线程
            ClientConnectServerThread clientConnectServerThread = ManagerClientConnectServerThread.getClientConnectServerThread(user.getUserId());
            Socket socket1 = clientConnectServerThread.getSocket();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserId());

        //发送message
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManagerClientConnectServerThread.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
            System.out.println(user.getUserId() + " 退出系统");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
