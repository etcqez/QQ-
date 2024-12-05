package qqcommon;

public interface MessageType {
    //定义了一些常量，不同的常量值表示不同的消息类型
    //表示登录成功
    String MESSAGE_LOGIN_SUCCEED = "1";
    //表示登录失败
    String MESSAGE_LOGIN_FAIL = "2";
    //普通信息包
    String MESSAGE_COMM_MES = "3";
    //要求返回在线用户列表
    String MESSAGE_GET_ONLINE_FRIEND = "4";
    String MESSAGE_RET_ONLINE_FRIEND = "5";
    String MESSAGE_CLIENT_EXIT = "6";
}
