package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

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

    public Socket getSocket() {
        return socket;
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

                    //私聊
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //根据Mess获取getterId，然后在得到对应的线程
                    //getterId 接收者
                    ServerConnectClientThread serverConnectClientThread = ManagerClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);

                    //群发
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    HashMap<String, ServerConnectClientThread> hashMap = ManagerClientThreads.getHashMap();

                    //遍历集合
                    Iterator<String> iterator = hashMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String onLineUserId = iterator.next().toString();
                        //排除群发消息的这个用户
                        if (!onLineUserId.equals(message.getSender())) {
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(hashMap.get(onLineUserId).getSocket().getOutputStream());
                            objectOutputStream.writeObject(message);
                        }
                    }
                    //发送文件
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    ServerConnectClientThread serverConnectClientThread = ManagerClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);

                } else {
                    System.out.println("暂时不处理");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
