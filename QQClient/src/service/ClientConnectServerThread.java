package service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


/**
 * 该类是线程类
 * 该类一直读取来自服务器的数据
 * 根据不同的消息类型读取不同的内容
 */
public class ClientConnectServerThread extends Thread {
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

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("\n" + message.getSender() + " 对 " + message.getGetter() + " 说 " + message.getContent());

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    System.out.println("\n" + message.getSender() + " 对大家说 " + message.getContent());

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter() + " 发文件：" + message.getSrc() + " 到我的电脑的目录 " + message.getDest());
                    FileOutputStream fileInputStream = new FileOutputStream(message.getDest());
                    fileInputStream.write(message.getFileBytes());
                    fileInputStream.close();
                    System.out.println("保存文件成功");

                }
                else {
                    System.out.println("是其他类型的message，暂时不处理");

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
