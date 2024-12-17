package qqclient.view;

import service.FileClientService;
import service.MessageClientService;
import service.UserClientService;
import utility.Utility;

import java.io.IOException;

//客启端的菜单界面
public class QQView {
    //控制是否显示菜单
    private boolean loop = true;
    //接收用户的键盘输入
    private String key = "";
    //登录，退出
    private UserClientService userClientService = new UserClientService();
    //私聊，群聊
    private MessageClientService messageClientService = new MessageClientService();
    //传输文件
    private FileClientService fileClientService = new FileClientService();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQView().mainMenu();
        System.out.println("客户端退出系统");
    }

    /**
     * 登录 -> 验证(属性：用户客户端服务 -> 发送user对象 -> 读取message)
     */
    private void mainMenu() throws IOException, ClassNotFoundException {
        while (loop) {
            System.out.println("==============欢迎登录网络通信系统==============");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");

            key = Utility.readString(1);

            //根据用户的输入来处理不同的逻辑
            switch (key) {
                case "1":
                    System.out.println("请输入用户号：");
                    String userid = Utility.readString(50);
                    System.out.println("请输入密码：");
                    String pwd = Utility.readString(50);
                    //需要到服务端验证用户是否合法
                    if (userClientService.checkUser(userid, pwd)) {
                        System.out.println("==============欢迎用户 " + userid + " 登录==============");
                        //进入到二级菜单
                        while (loop) {
                            System.out.println("\n==========网络通信系统二级菜单（用户：" + userid + "）===========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.println("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    userClientService.onlineFrientList();
                                    break;
                                case "2":
                                    System.out.println("请输入想对大家说的话");
                                    String s = Utility.readString(100);
                                    messageClientService.sendMessageToAll(s, userid);
                                    break;
                                case "3":
                                    System.out.println("请输入想聊天的用户号(在线)：");
                                    String getterId = Utility.readString(50);
                                    System.out.println("请输入想说的话: ");
                                    String content = Utility.readString(100);
                                    messageClientService.sendMessageToOne(content, userid, getterId);
                                    break;
                                case "4":
                                    System.out.print("请输入你想把文件发送给的用户(在线用户)");
                                    getterId = Utility.readString(50);
                                    System.out.print("请输入发送文件的路径(绝对路径)");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入文件发送到对应路径");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src, dest, userid, getterId);
                                    break;
                                case "9":
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        System.out.println("==============登录失败============");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
