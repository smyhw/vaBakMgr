package online.smyhw.vaBakMgr.adapter;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MojangAuthenticationService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.data.ProtocolState;
import com.github.steveice10.mc.protocol.data.game.BuiltinChatType;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.github.steveice10.packetlib.tcp.TcpServer;
import net.kyori.adventure.text.Component;
import online.smyhw.vaBakMgr.utils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class mcprotocollib implements base {
    Session client;
    private List<String> recv_msg_list = new CopyOnWriteArrayList();
    @Override
    public boolean init_ar(String ip, int port, String version, String username, String passwd) {
        MinecraftProtocol protocol;
        if (passwd == null) {
            try {
                AuthenticationService authService = new MojangAuthenticationService();
                authService.setUsername(username);
                authService.setPassword(passwd);
//                authService.setProxy(AUTH_PROXY);
                authService.login();

                protocol = new MinecraftProtocol(authService.getSelectedProfile(), authService.getAccessToken());
                utils.log("Successfully authenticated user.");
            } catch (RequestException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            protocol = new MinecraftProtocol(username);
        }

        SessionService sessionService = new SessionService();
//        sessionService.setProxy(AUTH_PROXY);

        client = new TcpClientSession(ip, port, protocol, null);
        client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {
                if (packet instanceof ClientboundLoginPacket) {
                    session.send(new ServerboundChatPacket("Hello, this is a test of MCProtocolLib.", Instant.now().toEpochMilli(), 0, new byte[0], false));
                } else if (packet instanceof ClientboundSystemChatPacket) {
                    Component message = ((ClientboundSystemChatPacket) packet).getContent();
                    recv_msg_list.add(message+"");
                    utils.log("Received Message: " + message);
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                utils.warning("[mcprotocollib]连接已断开 --> "+event.getReason(),2);
                if (event.getCause() != null) {
                    event.getCause().printStackTrace();
                }
            }
        });

        client.connect();
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
        this.client.send(new ServerboundChatPacket(msg, Instant.now().toEpochMilli(), 0, new byte[0], false));
        return false;
    }

    @Override
    public boolean dis_connect() {
        this.client.disconnect("vaBakMgr disconnect");
        return false;
    }

}