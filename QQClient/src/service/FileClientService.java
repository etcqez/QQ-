package service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileClientService {

    public void sendFileToOne(String src, String dest, String sendId, String gettId) {
        Message message = new Message();
        message.setDest(dest);
        message.setSrc(src);
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(sendId);
        message.setGetter(gettId);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //提示信息
        System.out.println("\n" + sendId + " 给 " + gettId + " 发送文件: " + src + " 到对方的电脑目录 " + dest);

        //发送
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManagerClientConnectServerThread.getClientConnectServerThread(sendId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
