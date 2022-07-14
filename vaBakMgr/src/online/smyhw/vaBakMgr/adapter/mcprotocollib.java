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
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;
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
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import online.smyhw.vaBakMgr.utils;

import java.net.Proxy;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class mcprotocollib implements base {
    private List<String> recv_msg_list = new CopyOnWriteArrayList();
    boolean is_ready = false;
    Session client;

    boolean is_disconnect = false;
    @Override
    public boolean init_ar(String ip, int port, String version, String username, String passwd) {
        MinecraftProtocol protocol;
        if (passwd!=null) {
            try {
                AuthenticationService authService = new MojangAuthenticationService();
                authService.setUsername(username);
                authService.setPassword(passwd);
                authService.setProxy(Proxy.NO_PROXY);
                authService.login();

                protocol = new MinecraftProtocol(authService.getSelectedProfile(), authService.getAccessToken());
                System.out.println("Successfully authenticated user.");
            } catch (RequestException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            protocol = new MinecraftProtocol(username);
        }

        SessionService sessionService = new SessionService();
        sessionService.setProxy(Proxy.NO_PROXY);

        client = new TcpClientSession(ip, port, protocol, null);
        client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {
                if (packet instanceof ClientboundLoginPacket) {
                    is_ready = true;
//                    session.send(new ServerboundChatPacket("H1ello, this is a test of MCProtocolLib.", Instant.now().toEpochMilli(), 0, new byte[0], false));
                } else if (packet instanceof ClientboundPlayerChatPacket) {
                    ClientboundPlayerChatPacket pkg = ((ClientboundPlayerChatPacket) packet);
                    Component message = pkg.getUnsignedContent() == null ? pkg.getSignedContent() : pkg.getUnsignedContent();
                    String plain = PlainTextComponentSerializer.plainText().serialize(message);
                    plain = "<"+PlainTextComponentSerializer.plainText().serialize(pkg.getSenderName())+"> "+plain;
//                    System.out.println("Received Message: " + plain);
                    recv_msg_list.add(plain+"");
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                is_disconnect = true;
                System.out.println("[mcprotocollib]Disconnected: " + event.getReason());
                if (event.getCause() != null) {
                    event.getCause().printStackTrace();
                }
            }
        });

        client.connect();
        while(!is_ready){
            if (is_disconnect){return false;}
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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