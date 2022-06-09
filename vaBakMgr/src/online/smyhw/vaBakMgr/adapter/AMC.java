package online.smyhw.vaBakMgr.adapter;

import net.defekt.mc.chatclient.protocol.AuthType;
import net.defekt.mc.chatclient.protocol.ClientListener;
import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.ProtocolNumber;
import net.defekt.mc.chatclient.protocol.data.ItemsWindow;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket;
import online.smyhw.vaBakMgr.Main;
import online.smyhw.vaBakMgr.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AMC implements base {
    private List<String> recv_msg_list = new CopyOnWriteArrayList();
    private MinecraftClient cl;
    @Override
    public boolean init_ar(String ip, int port, String version, String username, String passwd) {
        try {
            cl = new MinecraftClient(ip, port, ProtocolNumber.getForName(version).getProtocol(), false);
            if(passwd==null){
                cl.connect(username);
            }else{
                cl.connect(AuthType.Mojang,username,passwd);
            }

        } catch (IOException e) {
            utils.warning("和服务器建立连接失败 --> "+e.getMessage(),2);
//            e.printStackTrace();
            return false;
        }

        cl.addClientListener(new ClientListener() {

            @Override
            public void messageReceived(final String message, final ServerChatMessagePacket.Position pos) {
//                utils.log("来自服务器的数据包 --> " + message);
                recv_msg_list.add(message);
            }

            @Override
            public void disconnected(String s) {
                utils.warning("[AMC]连接已断开 --> "+s,2);
            }

            @Override
            public void healthUpdate(float v, int i) {

            }

            @Override
            public void positionChanged(double v, double v1, double v2) {

            }

            @Override
            public void statisticsReceived(Map<String, Integer> map) {

            }

            @Override
            public void windowOpened(int i, ItemsWindow itemsWindow, PacketRegistry packetRegistry) {

            }

            @Override
            public void timeUpdated(long l, long l1) {

            }

        });
        return true;
    }

    @Override
    public String get_msg() {
        if(recv_msg_list.isEmpty()){return null;}
        String re = recv_msg_list.get(0);
        recv_msg_list.remove(0);
        return re;
    }

    @Override
    public boolean send_msg(String msg) {
        try {
            cl.sendChatMessage(msg);
        } catch (IOException e) {
            utils.warning("向服务器发送消息时异常 -> "+ e.getMessage(),2);
            return false;
        }
        return true;
    }

    @Override
    public boolean dis_connect() {

        cl.close();
        return true;
    }
}
