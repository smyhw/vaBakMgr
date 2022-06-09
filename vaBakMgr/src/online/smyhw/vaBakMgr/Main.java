package online.smyhw.vaBakMgr;


import online.smyhw.vaBakMgr.adapter.AMC;
import online.smyhw.vaBakMgr.adapter.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import static online.smyhw.vaBakMgr.utils.*;

public class Main {
    public static int status = 1;

    public static base client;

    public static void main(String[] args) throws InterruptedException {

        log("Hello vaBakMgr !");

        init_client();

        //处理init_cmds
        List<String> init_cmds = null;
        try{
            init_cmds = (List<String>) utils.get_config().get("init_cmd");
        }catch(ClassCastException e){
            utils.warning("读取init_cmd配置项目失败 -> "+e.getMessage(),2);
            System.exit(1);
        }
        if(init_cmds!=null){
            for(String one_cmd : init_cmds){
                Main.client.send_msg(one_cmd);
            }
        }else{
            utils.warning("没有读取到init_cmd配置项目，跳过...",2);
        }

        while(status == 1){
            Thread.sleep(500);
            if(Main.client==null){continue;}
            String msg = client.get_msg();
            if(msg==null){continue;}
            utils.log("消息 -> " + msg);
            String cmd = utils.parse_msg(msg);
            cmd_mgr.parse_cmd(cmd);
        }
    }
    public static boolean init_client(){
        HashMap cfg = utils.get_config();
        String ip = (String) cfg.get("ip");
        if(ip==null){utils.warning("无法获取配置项目<ip>,请检查配置文件...");System.exit(1);}
        int port = 0;
        try{port = ((Double) cfg.get("port")).intValue();}catch(NumberFormatException e){utils.warning("配置项目<port>失败,请检查配置文件...");System.exit(1);}
        String version = (String) cfg.get("version");
        if(version==null){utils.warning("无法获取配置项目<version>,请检查配置文件...");System.exit(1);}
        String username = (String) cfg.get("username");
        if(username==null){utils.warning("无法获取配置项目<username>,请检查配置文件...");System.exit(1);}
        String passwd = (String) cfg.get("passwd");
        if(passwd!=null && passwd.equals("null")){passwd=null;}

        base b = get_client(ip, port, version,username, passwd);
        if(b==null){
            utils.warning("创建客户端失败");
            return false;
        }
        client = b;
        utils.log("客户端初始化完成");
        return true;
    }
}