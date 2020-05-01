package com.simplerobot.component.jcq

import com.forte.qqrobot.BaseApplication
import com.forte.qqrobot.MsgParser
import com.forte.qqrobot.MsgProcessor
import com.forte.qqrobot.beans.messages.msgget.MsgGet
import com.forte.qqrobot.bot.BotInfo
import com.forte.qqrobot.bot.BotManager
import com.forte.qqrobot.bot.BotSender
import com.forte.qqrobot.depend.DependCenter
import com.forte.qqrobot.listener.invoker.ListenerManager
import com.forte.qqrobot.log.QQLog
import com.forte.qqrobot.sender.MsgSender
import com.forte.qqrobot.sender.senderlist.RootSenderList
import com.simplerobot.component.jcq.log.JCQLog
import org.meowy.cqp.jcq.entity.CoolQ
import java.util.function.Function

/**
 * JCQ组件启动器
 */
open class JCQApplication(
        // 启动时的唯一CoolQ对象
        private val cq: CoolQ
) : BaseApplication<
        JCQConfiguration,
        JCQSender,
        JCQSender,
        JCQSender,
        JCQContext
        >() {

    /** 初始化送信器 */
    private val sender = JCQSender(cq)


    @Deprecated("only use getRootSenderFunction()", ReplaceWith("getRootSenderFunction()"))
    override fun getSetter(msgGet: MsgGet?, botManager: BotManager?): JCQSender? = null

    @Deprecated("only use getRootSenderFunction()", ReplaceWith("getRootSenderFunction()"))
    override fun getSender(msgGet: MsgGet?, botManager: BotManager?): JCQSender? = null

    @Deprecated("only use getRootSenderFunction()", ReplaceWith("getRootSenderFunction()"))
    override fun getGetter(msgGet: MsgGet?, botManager: BotManager?): JCQSender? = null

    /**
     * 由于CoolQ静态唯一，所以送信器唯一
     */
    override fun getRootSenderFunction(botManager: BotManager?): Function<MsgGet?, RootSenderList> = Function { sender }

    /**
     * 获取一个组件专属的SimpleRobotContext对象
     * @param defaultMsgSender 函数[getDefaultSender]的最终返回值
     * @param manager       botManager对象
     * @param msgParser     消息字符串转化函数
     * @param processor     消息处理器
     * @param dependCenter  依赖中心
     * @return 组件的Context对象实例
     */
    override fun getComponentContext(defaultsenders: DefaultSenders<JCQSender, JCQSender, JCQSender>, manager: BotManager, msgParser: MsgParser, processor: MsgProcessor, dependCenter: DependCenter): JCQContext = JCQContext(sender, manager, msgParser, processor, dependCenter, cq)


    /**
     * 开发者实现的获取Config对象实例的方法
     * 此方法将会最先被执行，并会将值保存，使用时可使用[.getConf] 方法获取
     */
    override fun getConfiguration(): JCQConfiguration = JCQConfiguration()


    /** 没啥好close的 */
    override fun close() { }

    /**
     * 开发者实现的资源初始化
     * 此方法将会在所有的初始化方法最后执行
     * 增加一个参数
     * 此资源配置将会在配置之后执行
     *
     * 暂时没啥好初始化的
     */
    override fun resourceInit(config: JCQConfiguration) {
        // 为config中初始化一个bot信息以触发bot账号注册
        config.registerBot(null, "Nothing but love")
    }

    /**
     * 开发者实现的资源初始化
     * 此方法将会在所有的无配置初始化方法最后执行
     * 将会在用户配置之前执行
     */
    override fun resourceInit() {}

    /**
     * 启动一个服务，这有可能是http或者是ws的监听服务
     * 然而这次啥都不是，所以没啥可启动的
     * @param dependCenter   依赖中心
     * @param manager        监听管理器
     * @param msgProcessor   送信解析器
     * @return
     */
    override fun runServer(dependCenter: DependCenter, manager: ListenerManager, msgProcessor: MsgProcessor, msgParser: MsgParser) = "simple-JCQ"

    /**
     * 验证？验证个锤子
     */
    override fun verifyBot(code: String?, info: BotInfo?): BotInfo = JCQBotInfo(sender.loginQQInfo, BotSender(sender), cq)


    /** 延迟初始化 默认送信器 */
    private lateinit var defMsgSender: MsgSender



    /**
     * 字符串转化为MsgGet的方法，最终会被转化为[MsgParser]函数，
     * 会作为参数传入[runServer], 也会封装进[JCQContext]中
     * @param str
     * @return
     */
    @Deprecated("just use base data", ReplaceWith("null"))
    override fun msgParse(str: String?): MsgGet? = null

    /**
     * @see [getDefaultSenders]
     */
    override fun getDefaultSetter(botManager: BotManager?): JCQSender? = null

    /**
     * @see [getDefaultSenders]
     */
    override fun getDefaultGetter(botManager: BotManager?): JCQSender? = null

    /**
     * @see [getDefaultSenders]
     */
    override fun getDefaultSender(botManager: BotManager?): JCQSender? = null


    /**
     * 重写此方法后上述三个方法将不会被使用。
     */
    override fun getDefaultSenders(botManager: BotManager?): DefaultSenders<JCQSender, JCQSender, JCQSender> = DefaultSenders(sender, sender, sender);

    override fun `_hello$`() {
        QQLog.info("Love yourself, love this world")
        JCQLog.info("Love this world, love yourself")
    }


}