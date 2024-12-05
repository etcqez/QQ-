package qqclient.service;

import java.util.HashMap;

/**
 * 该类是静态类
 * 该类管理客户端和到服务器端通信的线程的类
 */
public class ManagerClientConnectServerThread {
    //把多个线程放入到一个HashMap集合，key是用户id，value是线程
    private static HashMap<String,ClientConnectServerThread> hashMap = new HashMap<>();

    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        hashMap.put(userId,clientConnectServerThread);
    }

    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hashMap.get(userId);
    }
}
