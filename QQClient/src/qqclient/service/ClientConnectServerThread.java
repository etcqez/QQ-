package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


/**
 * 该类是线程类
 * 该类一直读取来自服务器的数据
 * 根据不同的消息类型读取不同的内容
 */
public class ClientConnectServerThread extends Thread{
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("客户端等待读取");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();
                //判断message类型，然后做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //取出在线列表信息
                    String[] onLineUsers = message.getContent().split(" ");
                    System.out.println("===============当前在线用户列表=============");
                    for (int i = 0; i < onLineUsers.length; i++) {

                        //规定服务器发送过来的格式：100 200 至尊宝 紫霞仙子
                        System.out.println("用户：" + onLineUsers[i]);
                    }


                }else {

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
