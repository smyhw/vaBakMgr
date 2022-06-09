package online.smyhw.vaBakMgr;

import online.smyhw.vaBakMgr.bak_core.file_thread;
import online.smyhw.vaBakMgr.bak_core.mgr;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cmd_mgr {
    public static void parse_cmd(String cmd){
        //
        if(!cmd.startsWith("#")){return;}
        String cmd_0 = "";
        String[] tmp1 = cmd.split(" ");
        if(tmp1.length<1){cmd_0 = cmd;}else{cmd_0=tmp1[0];}

        switch (cmd_0){
            case"#vbm":
                Main.client.send_msg("=====vaBakMgr=====");
                Main.client.send_msg("+ #bk <备注>   创建备份");
                Main.client.send_msg("+ #re <备份ID> 还原备份");
                Main.client.send_msg("+ #ls         查看备份列表");
                return;
            case"#ls":
                Main.client.send_msg("=====vaBakMgr=====");
                HashMap data = mgr.get_data_file();
                List<Map> data_list = (List<Map>) data.get("data");
                //格式化日期
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd-HH:mm");
                int max_len = 0;
                for(Map one_data : data_list){
                    long time = Long.parseLong((String) one_data.get("time"));
                    Date date = new Date();
                    date.setTime(time);
                    String date_txt = sdf.format(date);
                    if(date_txt.length()>max_len){max_len=date_txt.length();}
                }
                    //
                    for(Map one_data : data_list){
                        long time = Long.parseLong((String) one_data.get("time"));
                        Date date = new Date();
                        date.setTime(time);
                        String date_txt = sdf.format(date);
                        if(date_txt.length()<max_len){
                            for(int num=0;num<max_len-date_txt.length();num++){
                                date_txt = date_txt+" ";
                            }
                        }
                    Main.client.send_msg("*"+one_data.get("id")+" -> "+date_txt+" - "+one_data.get("note"));
                }
                return;
            case"#bk":
                if(cmd.split(" ").length!=2){
                    Main.client.send_msg("用法: #bk <备注>");
                    return;
                }
                mgr.backup_once(cmd.split(" ")[1]);
                return;
            case"#re":
                if(cmd.split(" ").length!=2){
                    Main.client.send_msg("用法: #re <备份id>");
                    return;
                }
                //解析id
                String id = cmd.split(" ")[1];

                //判定id是否存在
                boolean is_exist = false;
                for(HashMap one : (List<HashMap>) mgr.get_data_file().get("data")){
                    if(one.get("id").equals(id)){is_exist=true;}
                }
                if(!is_exist){
                    utils.warning("备份id<"+id+">不存在");
                    return;
                }

                //断开与服务器的连接
                Main.client.dis_connect();
                Main.client = null;

                //关闭服务器
                try {
                    String stop_cmd  = (String) utils.get_config().get("stop_cmd");
                    utils.log("run system cmd -> "+stop_cmd);
                    Runtime.getRuntime().exec(stop_cmd);
                } catch (IOException e) {
                    utils.warning("关闭服务器失败，异常 -> "+e.getMessage());
//                    e.printStackTrace();
                    return;
                }catch (ClassCastException e){
                    utils.warning("读取stop_cmd配置项失败，请检查配置文件 -> "+e.getMessage());
//                    e.printStackTrace();
                    return;
                }
                utils.log("关闭服务器执行完成");
                //备份目前的存档
                SimpleDateFormat sdf_1 = new SimpleDateFormat();
                sdf_1.applyPattern("yyyy-MM-dd-HH:mm:ss");
                Date date = new Date();
                mgr.backup_once("回档备份<"+id+"><"+sdf_1.format(date)+">");
                do {
                    utils.log("等待备份完成...");
                    try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
                }while(mgr.get_status());
                //还原存档
                if(!mgr.roll_back(id)){
                    utils.warning("警告,还原备份失败");
                    System.exit(1);
                }
                //开启服务器
                try {
                    String start_cmd  = (String) utils.get_config().get("start_cmd");
                    utils.log("run system cmd -> "+start_cmd);
                    Runtime.getRuntime().exec(start_cmd);
                } catch (IOException e) {
                    utils.warning("开启服务器失败，异常 -> "+e.getMessage());
//                    e.printStackTrace();
                    return;
                }catch (ClassCastException e){
                    utils.warning("读取start_cmd配置项失败，请检查配置文件 -> "+e.getMessage());
//                    e.printStackTrace();
                    return;
                }
                utils.log("启动服务器...");
                //重建连接
                while(!Main.init_client()){
                    utils.log("等待服务器启动...");
                    try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
                }
                return;
        }
    }
}
