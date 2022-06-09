> 项目涉及对存档的操作，且未经大量测试，请仍然手动保存你的存档以防止意外!!!

> 发现任何问题欢迎反馈

***
本项目被设计为在同学/朋友之间的小型minecraft纯原版服务器上运行，可以方便地 备份/管理/还原 存档，同时不对官方服务端做任何侵入式操作  
***
* 利用机器人实现游戏内指令交互
* 支持离线账户登入、mojang账号登入和皮肤站登入
* 1.12.2-1.19全版本支持
* 不对服务端做任何侵入性操作

# 食用方法
1. 下载一份发布包并解压缩
2. 编辑配置文件
3. 运行start.bat



# 配置文件
项目的配置文件位于项目根目录下的`config.json`  
```
{
  //需要连接的服务器ip
  "ip": "127.0.0.1",
  //需要连接的服务器端口
  "port": 25565,
  //服务器版本,目前支持1.12.2到1.18.2
  "version": "1.12.2",
  //如果是离线登入，则填写玩家id
  //如果是在线登入，则填写登入邮箱
  //*暂时不支持微软账户
  "username": "robot",
  //如果是在线登入，填入密码
  //如果是离线登入，填入null
  "passwd": "null",
  //需要备份的世界文件夹
  "save_world_dir": "./../world",
  //如果你的服务器需要登入之类的，可以在这里填写连接后自动执行的指令
  //支持填写多个，支持按顺序执行
  "init_cmd": ["/l 123456","robot login","/me hello world"],
  //当还原备份时，如何关闭你的服务器？
  //这将执行为系统shell
  "stop_cmd": "taskkill /fi \"WINDOWTitle eq hello_server*\"",
  //当还原成功后，如何重启你的服务器？
  //这将执行为系统shell
  "start_cmd": "cmd /k \"cd C:\\mc && start start.cmd\""
}
```

# 注意
* 项目涉及对存档的操作，且未经大量测试，请仍然手动保存你的存档以防止意外!!!
* 理论支持linux，但是没有经过测试
* 使用java11构建，请至少使用java11或以上java版本
* 目前没有权限系统，所有玩家均可使用所有指令 *(因为这是基于"同学/朋友之间的小型服务器"的信任环境而设计的)*

# TODO
* 权限管理

# 截图

# 依赖
本项目依赖以下开源项目
* [google](https://github.com/google) 的 [GSON](https://github.com/google/gson)，以 [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0)开源  
* [Defective4](https://github.com/Defective4) 的 [Another Minecraft Chat Client](https://github.com/Defective4/Another-Minecraft-Chat-Client)，以 [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0)开源