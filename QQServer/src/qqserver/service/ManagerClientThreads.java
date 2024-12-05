package qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 管理在线用户列表
 */
public class ManagerClientThreads {
    private static HashMap<String, ServerConnectClientThread> hashMap = new HashMap<>();


    public static void addClientConnectServerThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hashMap.put(userId, serverConnectClientThread);
    }

    public static ServerConnectClientThread getClientConnectServerThread(String userId) {
        return hashMap.get(userId);
    }

    public static void removeClientConnectServerThread(String userId) {
        hashMap.remove(userId);
    }

    public static String getOnlineUsers() {
        //集合遍历
        Iterator<String> iterator = hashMap.keySet().iterator();
        String onLineUsers = "";
        while (iterator.hasNext()) {
            onLineUsers += iterator.next().toString() +" ";
        }
        return onLineUsers;
    }
}
