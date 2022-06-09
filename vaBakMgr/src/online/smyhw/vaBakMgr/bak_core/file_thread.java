package online.smyhw.vaBakMgr.bak_core;

import online.smyhw.vaBakMgr.Main;
import online.smyhw.vaBakMgr.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 代表一个正在运行的备份
 */
public class file_thread extends Thread {
    private String div_name;
    public file_thread(String div_name){
        mgr.is_running = true;
        String from = (String) utils.get_config().get("save_world_dir");
        File f = new File(from);
        String tmp1 = f.getName();
        this.div_name = "./save/"+div_name+"/"+tmp1;
        this.start();
    }

    @Override
    public void run(){
        String from = (String) utils.get_config().get("save_world_dir");
        String to = div_name;
        try {
            utils.copy_dir(from,to);
        } catch (IOException e) {
            utils.log("创建备份异常 -> "+e.getMessage(),1);
//            e.printStackTrace();
        }
        mgr.is_running = false;
        utils.log("备份完成...",1);
    }
}
