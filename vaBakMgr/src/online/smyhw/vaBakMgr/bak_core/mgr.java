package online.smyhw.vaBakMgr.bak_core;

import com.google.gson.Gson;
import online.smyhw.vaBakMgr.Main;
import online.smyhw.vaBakMgr.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class mgr {
    //是否有一个正在运行的备份
    public static boolean is_running = false;

    /**
     * 异步方法，根据给定的note创建一个备份
     * @param note
     * @return
     */
    public static boolean backup_once(String note){
        if(is_running){
            utils.warning("拒绝执行备份 -> 上一次备份尚未完成...");
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd-HH:mm");
        Date date = new Date();
        utils.log("创建备份 -> 时间>" + sdf.format(date) + "< , 备注 >"+note+"<");
        //操作数据文件
        HashMap data = get_data_file();
        int id = (int) ((double)data.get("id_num"));//FK
        id++;
        List<HashMap> bk_list = (List) data.get("data");
        HashMap tmp1 = new HashMap();
        tmp1.put("time",date.getTime()+"");
        tmp1.put("id",id+"");
        tmp1.put("note",note);
        bk_list.add(tmp1);
        data.put("id_num",(double)id);
        flush_data_file();
        //执行复制
        new file_thread(id+"");
        return true;
    }

    /**
     * 同步方法，根据给定的id还原一个备份
     * @param id
     * @return
     */
    public static boolean roll_back(String id){
        if(is_running){
            utils.warning("拒绝执行还原 -> 上一次备份尚未完成...");
            return false;
        }

        String to = (String) utils.get_config().get("save_world_dir");
        File f = new File(to);
        String tmp1 = f.getName();
        String from = "./save/"+id+"/"+tmp1;

        try {
            utils.copy_dir(from,to);
        } catch (IOException e) {
            utils.warning("警告,还原过程中出现错误! -> "+e.getMessage(),2);
            System.exit(1);
        }
        return true;
    }

    /**
     * 检查是否有正在运行的备份
     * @return 如果有，则返回true，否则false
     */
    public static boolean get_status(){
        return is_running;
    }

    private static HashMap data_map=null;
    public static HashMap get_data_file(){
        if(data_map!=null){return data_map;}
        try {
            File tmp1 = new File("./save");
            if(!(tmp1.exists()&&tmp1.isDirectory())){tmp1.mkdir();}
            tmp1 = new File("./save/data.json");
            if(!(tmp1.exists()&&tmp1.isFile())){
                tmp1.createNewFile();
                HashMap tmp2 = new HashMap();
                List tmp3 = new CopyOnWriteArrayList<>();
                tmp2.put("data",tmp3);
                tmp2.put("id_num",(double)1);
                data_map = tmp2;
                flush_data_file();
            }
        } catch (IOException e) {
            utils.warning("初始化数据文件异常 --> "+e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }

        Gson gson = new Gson();
        String data_txt = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./save/data.json"))));
            String line = br.readLine();
            while(line!=null){
                data_txt = data_txt+line;
                line = br.readLine();
            }
        } catch (IOException e) {
            utils.warning("数据文件读取错误，异常 -> "+e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
        HashMap data_json=null;
        try {
            data_json = gson.fromJson(data_txt, HashMap.class);
        }catch(com.google.gson.JsonSyntaxException e){
            utils.warning("数据文件解析错误，异常 -> "+e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
        data_map = data_json;
        return data_json;
    }

    public static boolean flush_data_file(){
        Gson gson = new Gson();
        String txt = gson.toJson(data_map);
        try {
            FileWriter fw = new FileWriter(new File("./save/data.json"));
            fw.write(txt);
            fw.close();
        } catch (IOException e) {
            utils.warning("数据文件写入出错,异常 -> "+e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
        return true;
    }

}
