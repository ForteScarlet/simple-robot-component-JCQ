# JCQ组件文档

**\[ JCQ FOR JAVA/KT \]**

[![](https://img.shields.io/badge/simple--robot-core-green)](https://github.com/ForteScarlet/simple-robot-core) [![](https://img.shields.io/maven-central/v/io.github.fortescarlet.simple-robot-component/component-jcq)](https://search.maven.org/artifact/io.github.fortescarlet.simple-robot-component/component-jcq)

**JCQ组件是一个基于**[**酷Q平台**](https://cqp.cc/forum.php)**、**[**JCQ插件**](https://github.com/Meowya/JCQ-CoolQ)**、**[**simple-robot核心库**](https://github.com/ForteScarlet/simple-robot-core)**的Java/kotlin QQ机器人开发框架。**

*****

**文档首页**：[JCQ组件](https://www.kancloud.cn/forte-scarlet/simple-coolq-doc/1638828)
　
*****

**JCQ组件 快速开始**：[JCQ组件-快速开始](https://www.kancloud.cn/forte-scarlet/simple-coolq-doc/1638829)

*****

- **基于酷Q平台，所以需要依赖于酷Q。**

- **基于酷Q平台的JCQ插件，此组件使用JCQ插件并依赖于此插件，提供此插件提供的所有API。**

- **基于simple-robot核心库，提供核心库提供的一切功能与特性。**

- **使用酷Q原生函数，API支持量较少**

- **不支持多账号功能**

<br><br>

# 快速开始

## 一、**安装**

### 1\. **下载并安装 酷Q**

前往酷Q[官方下载地址](https://cqp.cc/t/23253)下载酷Q应用，并安装（启动一次），然后关闭。

<br>


### 2\. **下载并安装 JCQ插件**
关于JCQ的安装、使用与配置，请参考JCQ的官方社区贴 [https://cqp.cc/t/37318](https://cqp.cc/t/37318)

### 3\. **创建Java项目**
（此教程为核心1.11.x以上版本。如何判断核心版本参考[核心版本系与升级](.组件如何升级核心.md/)）
你可以使用一切支持的方式来自动构建项目，以下将会举几个例子：

>[danger] 在你创建Java项目之前，我要先提前提JCQ声明：JCQ仅支持1.6以上的、32位的JDK(jre)，而simple-robot主要基于java1.8编写，因此请准备好一个32位的、1.8或以上版本的JDK(jre)
 

>[info]maven仓库地址参考：
> ①. [https://search.maven.org/artifact/io.github.fortescarlet.simple-robot-component/component-jcq](https://search.maven.org/artifact/io.github.fortescarlet.simple-robot-component/component-jcq)
> ②. [https://mvnrepository.com/artifact/io.github.ForteScarlet.simple-robot-component/component-jcq](https://mvnrepository.com/artifact/io.github.ForteScarlet.simple-robot-component/component-jcq)
 

#### **①. Maven**

```xml
        <dependency>
            <groupId>io.github.fortescarlet.simple-robot-component</groupId>
            <artifactId>component-jcq</artifactId>
            <version>${version}</version>
        </dependency>
```

#### **②. Gradle**

```
compile group: 'io.github.fortescarlet.simple-robot-component', name: 'component-jcq', version: '${version}'
```

#### **③. Grape**

```
@Grapes(
    @Grab(group='io.github.fortescarlet.simple-robot-component', module='component-jcq', version='${version}')
)
```

<br>

#### 以maven为例
根据JCQ所提供的maven-demo稍作修改，将pom.xml修改成大致如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.test</groupId>
    <artifactId>demo</artifactId>
    <version>1.0</version>

    <properties>
        <component-jcq.version>0.1-1.11</component-jcq.version>
    </properties>

    <dependencies>
        <!-- component-JCQ组件坐标 -->
        <dependency>
            <groupId>io.github.fortescarlet.simple-robot-component</groupId>
            <artifactId>component-jcq</artifactId>
            <version>${component-jcq.version}</version>
        </dependency>
    </dependencies>

    <build>
        <!-- 此处填写打包后的jar文件名，默认为当前项目的坐标名称 -->
        <finalName>${groupId}.${artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <overWriteReleases>false</overWriteReleases>
                            <overWriteSnapshots>false</overWriteSnapshots>
                            <overWriteIfNewer>true</overWriteIfNewer>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

创建与最终导出的文件名相对应的包路径与主类，以上述例子的话即`com.test.Demo`，并继承一个抽象类`com.simplerobot.component.jcq.JCQMainApplication`
```java
public class Demo extends JCQMainApplication {
    // 啥也不用写
}
```
一般来讲，如果没有什么需要进行额外操作或配置的东西，则无需再编写其他内容。如果想要进行本地测试，可以参照官方Demo中的测试方式：
```java
public class Demo extends JCQMainApplication {
    /**
     * 用main方法调试可以最大化的加快开发效率，检测和定位错误位置<br/>
     * 以下就是使用Main方法进行测试的一个简易案例
     *
     * @param args 系统参数
     */
    public static void main(String[] args) {
        // CQ此变量为特殊变量，在JCQ启动时实例化赋值给每个插件，而在测试中可以用CQDebug类来代替他
        CQ = new CQDebug();//new CQDebug("应用目录","应用名称") 可以用此构造器初始化应用的目录
        CQ.logInfo("[JCQ] TEST Demo", "测试启动");// 现在就可以用CQ变量来执行任何想要的操作了
        // 要测试主类就先实例化一个主类对象
        Demo demo = new Demo();
        // 下面对主类进行各方法测试,按照JCQ运行过程，模拟实际情况
        demo.startup();// 程序运行开始 调用应用初始化方法
        demo.enable();// 程序初始化完成后，启用应用，让应用正常工作
        // 开始模拟发送消息
        // 模拟私聊消息
        // 开始模拟QQ用户发送消息，以下QQ全部编造，请勿添加
        demo.privateMsg(0, 10001, 2234567819L, "小姐姐约吗", 0);
        demo.privateMsg(0, 10002, 2222222224L, "喵呜喵呜喵呜", 0);
        demo.privateMsg(0, 10003, 2111111334L, "可以给我你的微信吗", 0);
        demo.privateMsg(0, 10004, 3111111114L, "今天天气真好", 0);
        demo.privateMsg(0, 10005, 3333333334L, "你好坏，都不理我QAQ", 0);
        // 模拟群聊消息
        // 开始模拟群聊消息
        demo.groupMsg(0, 10006, 3456789012L, 3333333334L, "", "菜单", 0);
        demo.groupMsg(0, 10008, 3456789012L, 11111111114L, "", "小喵呢，出来玩玩呀", 0);
        demo.groupMsg(0, 10009, 427984429L, 3333333334L, "", "[CQ:at,qq=2222222224] 来一起玩游戏，开车开车", 0);
        demo.groupMsg(0, 10010, 427984429L, 3333333334L, "", "好久不见啦 [CQ:at,qq=11111111114]", 0);
        demo.groupMsg(0, 10011, 427984429L, 11111111114L, "", "qwq 有没有一起开的\n[CQ:at,qq=3333333334]你玩嘛", 0);
//         ......
//         依次类推，可以根据实际情况修改参数，和方法测试效果
//         以下是收尾触发函数
        demo.disable();// 实际过程中程序结束不会触发disable，只有用户关闭了此插件才会触发
        demo.exit();// 最后程序运行结束，调用exit方法
    }
}
```

然后在此`Demo`类旁边创建一个`listener`包，新建一个`TestListener`类，来写一个监听器：
> 就像使用`simple-robot`的其他组件一样
```java
/**
 * Demo监听器类
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Beans
public class TestListener {
    /**
     * Demo监听器，监听开头为'hello'的私信消息并复读
     */
    // 监听私信消息
    @Listen(MsgGetTypes.privateMsg)
    // 监听消息开头为"hello"的消息
    @Filter(value = "hello", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void priTest(PrivateMsg pmsg, MsgSender sender){
        // 复读私信消息
        sender.SENDER.sendPrivateMsg(pmsg, pmsg.getMsg());
    }

}
```



然后使用Maven的package进行打包。由于添加了`maven-dependency-plugin`插件，打包后的结果大致为：
![](https://i.vgy.me/xDHORy.png)
可以看到，`com.test.demo.jar`即为项目jar包，而`lib/`文件夹下的内容便是maven中所导入的依赖。

此时，先**删除**`lib/`目录下的`jcq-coolq-x.x.x.jar`文件。

#### JCQ Json配置
JCQ需要有一个`.json`格式的配置文件，具体格式可以参考JCQ的官方介绍：[https://gitee.com/meowy/JCQ-CoolQ#json-%E6%96%87%E4%BB%B6%E4%BB%8B%E7%BB%8D](https://gitee.com/meowy/JCQ-CoolQ#json-%E6%96%87%E4%BB%B6%E4%BB%8B%E7%BB%8D)
根据上述示例，创建一个`com.test.demo.json`文件（根据个人经验，此配置文件似乎需要使用`GBK`编码）
内容如下：
```json
// 酷Q 的Json文件支持以 // 开头的注释。
// 打包前，应用的 .jar, .json 的文件名须以appid命名，应用AppInfo返回的内容须改为appid
// 如 appid=com.example.demo, 则jar及json文件需分别命名为 com.example.demo.jar、com.example.demo.json
{
  "ret": 1, // 返回码，固定为1
  "apiver": 9, // Api版本，本SDK为9
  "name": "demo", // 应用名称
  "version": "1.0.0", // 应用版本
  "version_id": 1, // 应用顺序版本（每次发布时至少+1）
  "author": "ForteScarlet", // 应用作者
  //"path": "lib",// 应用lib加载处，默认插件同名文件夹目录下lib文件夹，此目录可以填相对路径和绝对路径，相对：应用同名文件夹
  //"class": "com.test.demo", // 应用加载主类，默认使用appid加载，如需使用则删除前面注释
  "description": "这是个JavaDemo插件",
  "event": [ // 事件列表，同一事件类型可重复定义（发布前请删除无用事件）
    {
      "id": 1, // 事件ID
      "type": 21, // 事件类型
      "name": "私聊消息处理", // 事件名称
      "function": "privateMsg", // 事件对应函数
      // 事件优先级(参见 cq.im/deveventpriority)
      "priority": 30000
    },
    {
      "id": 2,
      "type": 2,
      "name": "群消息处理",
      "function": "groupMsg",
      "priority": 30000
    },
    {
      "id": 3,
      "type": 4,
      "name": "讨论组消息处理",
      "function": "discussMsg",
      "priority": 30000
    },
    {
      "id": 4,
      "type": 11,
      "name": "群文件上传事件处理",
      "function": "groupUpload",
      "priority": 30000
    },
    {
      "id": 5,
      "type": 101,
      "name": "群管理变动事件处理",
      "function": "groupAdmin",
      "priority": 30000
    },
    {
      "id": 6,
      "type": 102,
      "name": "群成员减少事件处理",
      "function": "groupMemberDecrease",
      "priority": 30000
    },
    {
      "id": 7,
      "type": 103,
      "name": "群成员增加事件处理",
      "function": "groupMemberIncrease",
      "priority": 30000
    },
    {
      "id": 10,
      "type": 201,
      "name": "好友已添加事件处理",
      "function": "friendAdd",
      "priority": 30000
    },
    {
      "id": 8,
      "type": 301,
      "name": "好友添加请求处理",
      "function": "requestAddFriend",
      "priority": 30000
    },
    {
      "id": 9,
      "type": 302,
      "name": "群添加请求处理",
      "function": "requestAddGroup",
      "priority": 30000
    },
    {
      "id": 1001,
      "type": 1001,
      "name": "酷Q启动事件",
      "priority": 30000,
      "function": "startup"
    },
    {
      "id": 1002,
      "type": 1002,
      "name": "酷Q关闭事件",
      "priority": 30000,
      "function": "exit"
    },
    {
      "id": 1003,
      "type": 1003,
      "name": "应用已被启用",
      "priority": 30000,
      "function": "enable"
    },
    {
      "id": 1004,
      "type": 1004,
      "name": "应用将被停用",
      "priority": 30000,
      "function": "disable"
    }
  ],
  "status": [
    // 悬浮窗状态（见 com.example.status 样例）
  ],
  "auth": [ // 应用权限（发布前请删除无用权限）
    20,  //[敏感]取Cookies    getCookies / getCsrfToken
    30,  //接收语音            getRecord
    101, //发送群消息            sendGroupMsg
    103, //发送讨论组消息        sendDiscussMsg
    106, //发送私聊消息        sendPrivateMsg
    110, //发送赞                sendLike
    120, //置群员移除            setGroupKick
    121, //置群员禁言            setGroupBan
    122, //置群管理员            setGroupAdmin
    123, //置全群禁言            setGroupWholeBan
    124, //置匿名群员禁言        setGroupAnonymousBan
    125, //置群匿名设置        setGroupAnonymous
    126, //置群成员名片        setGroupCard
    //127, //[敏感]置群退出        setGroupLeave
    128, //置群成员专属头衔    setGroupSpecialTitle
    130, //取群成员信息        getGroupMemberInfoV2 / getGroupMemberInfo
    131, //取陌生人信息        getStrangerInfo
    140, //置讨论组退出        setDiscussLeave
    150, //置好友添加请求        setFriendAddRequest
    151, //置群添加请求        setGroupAddRequest
    160, //取群成员列表        getGroupMemberList
    161, //取群列表            getGroupList
    180 //撤回消息            deleteMsg
  ]
}
```
需要注意的是，上述文件中的`event`(事件列表)参数中的各个`function`(事件函数名)请不要随意修改，否则可能会导致监听失效。

### **4\. 使用**
前往路径：`${酷Q根路径}\data\app\com.sobte.cqp.jcq\app`，即JCQ插件的目录下，将上述的`com.test.demo.jar` 与 `com.test.demo.json`文件粘贴进来，并创建一个文件夹`com.test.demo`，如图所示：
![](https://i.vgy.me/JPKSko.png)

其中，`com.test.demo`文件夹中，再创建一个`lib`文件夹，并将之前打包后得到的lib文件夹中的所有`.jar`依赖拷贝至此路径下。(**记得删除`jcq-coolq-x.x.x.jar`文件**)，结果大致如图所示：
![](https://i.vgy.me/ZsePxi.png)

结束后，启动酷Q，并启用插件：
![](https://i.vgy.me/yKBZju.png)

如果启动过程没有出现异常，且控制台中的日志正常，则说明启动成功了，可以进行功能测试与进一步的开发了。

## **5\. 失败了？**

如果跟着上述流程完整无误的操作却无法成功，也不要气馁，尝试根据[JCQ社区贴](https://cqp.cc/t/37318) 与 [常见问题汇总](./%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98%E6%B1%87%E6%80%BB.md)进行排查或者加入QQ群`782930037`进行咨询。




