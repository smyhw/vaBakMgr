package online.smyhw.vaBakMgr.adapter;

public interface base {
    /**
     * @param ip       服务器ip
     * @param port     服务器端口
     * @param username 如果离线登入，则直接传入玩家id，在线登入则传入用户名
     * @param passwd   在线登入时传入密码，否则忽略
     * @return
     */
    boolean init_ar(String ip, int port, String version, String username, String passwd);
    String get_msg();
    boolean send_msg(String msg);

    boolean dis_connect();
}
