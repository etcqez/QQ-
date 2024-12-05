package qqclient.view;

import qqclient.service.UserClientService;
import qqclient.utility.Utility;

import java.io.IOException;

//客启端的菜单界面
public class QQView {
    //控制是否显示菜单
    private boolean loop = true;
    //接收用户的键盘输入
    private String key = "";
    private UserClientService userClientService = new UserClientService();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQView().mainMenu();
        System.out.println("客户端退出系统");
    }

    /**
     * 登录 -> 验证(属性：用户客户端服务 -> 发送user对象 -> 读取message)
     * @throws IOException
     * @throws ClassNotFoundException
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
                    if (userClientService.checkUser(userid,pwd)) {
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
                                    System.out.println("群发消息");
                                    break;
                                case "3":
                                    System.out.println("私聊消息");
                                    break;
                                case "4":
                                    System.out.println("发送文件");
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
