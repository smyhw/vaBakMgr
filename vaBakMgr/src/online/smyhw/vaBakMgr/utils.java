package online.smyhw.vaBakMgr;

import com.google.gson.Gson;
import online.smyhw.vaBakMgr.adapter.AMC;
import online.smyhw.vaBakMgr.adapter.base;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class utils {

    /**
     * 复制文件夹
     * @param from
     * @param to
     * @param options
     * @throws IOException
     */
    public static void copy_dir(String from, String to, CopyOption... options) throws IOException {
        utils.warning("复制文件夹: from <"+from+"> to <"+to+">",2);
        Path source = Paths.get(from);
        Path target = Paths.get(to);
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 解析消息
     * @return
     */
    public static String parse_msg(String msg){
        String[] tmp1 = msg.split("> ");
        if(tmp1.length<2){return "";}
        String tmp2 = msg.replace(tmp1[0]+"> ","");
        return tmp2;
    }

    /**
     * 获取客户端实例，成功返回实例，失败返回null
     * @param version
     * @return
     */
    public static base get_client(String ip, int port, String version, String username, String passwd){
        base re = null;
        switch(version){
            case"1.12.2":
                re = new AMC();
        }
        boolean tmp1 = re.init_ar(ip,port,version,username,passwd);
        if(tmp1){return re;}else{return null;}
    }

    /**
     * 日志info
     * @param msg
     */
    public static void log(String msg,int... sync) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("HH:mm:ss");
        Date date = new Date();
        System.out.println("["+sdf.format(date)+"][I]- "+msg);
        if(Main.client!=null&&sync.length>0&&sync[0]==1){Main.client.send_msg(msg);}
    }

    /**
     * 日志warning
     * @param msg
     */
    public static void warning(String msg,int... sync){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("HH:mm:ss");
        Date date = new Date();
        System.out.println("["+sdf.format(date)+"][W]- "+msg);
        if(sync.length>0 && sync[0]==2){return;}
        if(Main.client!=null){Main.client.send_msg(msg);}
    }

    private static HashMap config;
    /**
     * 从硬盘读取配置文件</br>
     * @return
     */
    public static HashMap get_config() {
        if(config!=null){return config;}
        Gson gson = new Gson();
        String cfg_txt = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./config.json"))));
            String line = br.readLine();
            while(line!=null){
                //判断注释
                if(line.trim().startsWith("#") || line.trim().startsWith("//")){
                    line = br.readLine();
                    continue;
                }
                cfg_txt = cfg_txt+line;
                line = br.readLine();
            }
        } catch (IOException e) {
            log("配置文件读取错误，异常 -> "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        HashMap cfg_map=null;
        try {
            cfg_map = gson.fromJson(cfg_txt, HashMap.class);
        }catch(com.google.gson.JsonSyntaxException e){
            log("配置文件解析错误，异常 -> "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        config = cfg_map;
        return config;
    }
}
