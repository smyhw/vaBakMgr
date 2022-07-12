> 项目涉及对存档的操作，且未经大量测试，请仍然手动保存你的存档以防止意外!!!

> 发现任何问题欢迎反馈

***
本项目被设计为在同学/朋友之间的小型minecraft官方服务端服务器上运行,可以方便地 备份/管理/还原 存档,同时不对服务端做任何侵入式操作  
***
* 利用机器人实现游戏内指令交互
* 支持离线账户登入、mojang账号登入和皮肤站登入
* 1.12.2-1.19全版本支持
* 不对服务端做任何侵入性操作

# 食用方法
1. 下载一份发布包并解压缩
2. 编辑配置文件
3. 运行start.bat，此时机器人应该会登入服务器
4. 在服务器聊天中输入`#vbm`，机器人应该会返回帮助消息


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
* 在高版本中如果出现类似于  
`world\session.lock: 另一个程序已锁定文件的一部分，进程无法访问。`  
这样的报错，则可以忽视，目前来说这是正常情况

# TODO
* 权限管理

# 皮肤站登入
如果需要使用皮肤站登入，你需要一份`authlib-injector`，  
对，就是让你的服务器支持皮肤站登入的那个东西，直接从你的服务端里头复制一份过来都行，  
  
然后和你修改服务端启动参数时一样，  
编辑`start.bat`，在启动参数中加入`-javaagent:{你的authlib-injector文件位置}={你的皮肤站地址}`。  
最后，配置文件中的`username`和`passwd`处填入邮箱和密码，完成！

# 截图
![pic3](https://user-images.githubusercontent.com/47166461/173103526-2ca03bd8-faa9-4c42-b56a-2b03760a3b9f.png)
![pic2](https://user-images.githubusercontent.com/47166461/173103522-b79dd999-f4f1-4a0a-bdae-4295e7efd36a.png)
![pic1](https://user-images.githubusercontent.com/47166461/173103510-a36bb539-2651-401a-8000-07d6822d9351.png)

# FAQ
Q: 为什么菜单不进行染色处理？  
A: 原版服务器不支持客户端在聊天信息中插入染色符号，强制发送会被服务器以非法聊天字符为理由踢出  
  
Q: 为什么不支持微软账户登入？  
A: 上游协议库没支持，等它们更新  
  
Q: 我的服务器存档不止一个文件夹，如何备份多个文件夹？  
A: 目前是为官方服务端设计的，官方服务端存档只有一个文件夹，未来可能会支持多文件夹备份

# 依赖
本项目依赖以下开源项目
* [google](https://github.com/google) 的 [GSON](https://github.com/google/gson)，以 [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0)开源  
* [Defective4](https://github.com/Defective4) 的 [Another Minecraft Chat Client](https://github.com/Defective4/Another-Minecraft-Chat-Client)，以 [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0)开源
* [间歇泉项目](https://github.com/GeyserMC) 的 协议部分 ，以 [MIT](https://github.com/GeyserMC/MCProtocolLib/blob/master/LICENSE.txt)开源
