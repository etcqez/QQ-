package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 该类的一个对象和某个客户端保持通信
 * 登录成功后通信线程
 */
public class ServerConnectClientThread extends Thread {

    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("服务器和客户端" + userId + "保持通信");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                //客户端要在线用户列表
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUsers = ManagerClientThreads.getOnlineUsers();
                    Message message1 = new Message();
                    message1.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message1.setContent(onlineUsers);
                    //设置接收者
                    message1.setGetter(message.getSender());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(message1);

                    //退出
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + " 退出");
                    ManagerClientThreads.removeClientConnectServerThread(message.getSender());
                    socket.close();
                    break;
                }
                else {
                    System.out.println("暂时不处理");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
