package com.simplerobot.component.jcq

import com.forte.lang.Language
import com.forte.plusutils.consoleplus.FortePlusPrintStream
import com.forte.qqrobot.MsgProcessor
import com.forte.qqrobot.listener.result.ListenResult
import com.forte.qqrobot.log.QQLog
import com.forte.qqrobot.sender.MsgSender
import com.forte.qqrobot.utils.CQCodeUtil
import com.simplerobot.component.jcq.log.JCQLog
import com.sobte.cqp.jcq.entity.ICQVer
import com.sobte.cqp.jcq.entity.IMsg
import com.sobte.cqp.jcq.entity.IRequest
import com.sobte.cqp.jcq.event.JcqAppAbstract
import java.time.LocalDateTime

fun ListenResult<*>?.toResult() = if (this?.isToBreakPlugin == true) {
    IMsg.MSG_INTERCEPT
} else {
    IMsg.MSG_IGNORE
}

object Lang {

    private fun init() {
        if (!Language.already()) {
            Language.init()
        }
    }

    infix fun by(tag: String): String {
        init()
        return Language.format(tag)
    }

    infix fun by(tagAndFormats: Array<*>): String? {
        if (tagAndFormats.isEmpty()) {
            return null
        }
        init()
        if (tagAndFormats.size == 1) {
            return Language.format(tagAndFormats[0].toString())
        }
        val tag = tagAndFormats[0].toString()
        val formats = tagAndFormats.filterIndexed { i, _ -> i > 0 }
        return Language.format(tag, *formats.toTypedArray())
    }

    fun by(tag: String, vararg format: String): String {
        init()
        return Language.format(tag, *format)
    }

}

/**
 * JCQ主启动类, 作为抽象类使用
 *
 * 其中，部分可继承方法被替换了：
 * [startup] -> [onStartUp] , [enable] -> [onEnable] , [disable] -> [onDisable] , [exit] -> [onExit]
 *
 * Jcqapplication
 */
abstract class JCQMain : JcqAppAbstract(), ICQVer, IMsg, IRequest, JCQBotApp {

    /** 当前类的ID */
    val ID: String = this.hashCode().toString()

    /**
     * 启动器前置配置工作，默认以当前主类所在位置进行扫描
     * 可重写此方法以实现自定义配置
     */
    override fun before(conf: JCQConfiguration) {
        conf.setScannerPackage(this::class.java.`package`.name)
    }

    /**
     * 启动器启动后的操作
     * 打印一句日志
     */
    override fun after(cqCodeUtil: CQCodeUtil, sender: MsgSender) {
        JCQLog.info((Lang by "run.after"))
    }

    /** 延迟初始化 app目录 */
    lateinit var appDirectory: String private set

    /**
     * 延迟初始化 JCQ context
     * [startup]后被初始化
     * */
    lateinit var context: JCQContext private set

    /**
     * 消息处理器
     */
    val processor: MsgProcessor
        get() = context.msgProcessor

    // appID, 默认为当前类的类路径（类名开头小写
    open val appID: String
        get() = "${this::class.java.`package`.name}.${this::class.java.simpleName.decapitalize()}"

    /** 初始化 */
    private fun initJCQComponent() {
        // 初始化
        if (!::context.isInitialized) {
            context = JCQApplication().run(this)
        }
    }

    /**
     * 返回应用的ApiVer、Appid，打包后将不会调用<br></br>
     * 本函数【禁止】处理其他任何代码，以免发生异常情况。<br></br>
     * 如需执行初始化代码请在 startup 事件中执行（Type=1001）。<br></br>
     * 应用AppID,规则见 http://d.cqp.me/Pro/开发/基础信息#appid
     * 记住编译后的文件和json也要使用appid做文件名
     *
     * @return 应用信息
     */
    override fun appInfo(): String = ICQVer.CQAPIVER.toString() + "," + appID

    /**
     * 酷Q退出 (Type=[1002][IType.EVENT_Exit])<br></br>
     * 本方法会在酷Q【主线程】中被调用。<br></br>
     * 无论本应用是否被启用，本函数都会在酷Q退出前执行一次，请在这里执行插件关闭代码。
     *
     * @return 请固定返回0，返回后酷Q将很快关闭，请不要再通过线程等方式执行其他代码。
     */
    override fun exit(): Int {
        JCQLog.info("酷Q已终止")
        onExit()
        return 0
    }

    /** 可重写的 酷Q退出方法 */
    open fun onExit() {}

    /**
     * 应用已被启用 (Type=[1003][IType.EVENT_Enable])<br></br>
     * 当应用被启用后，将收到此事件。<br></br>
     * 如果酷Q载入时应用已被启用，则在 [startup][startup](Type=1001,酷Q启动) 被调用后，本函数也将被调用一次。<br></br>
     * 如非必要，不建议在这里加载窗口。
     *
     * @return 请固定返回0。
     */
    final override fun enable(): Int {
        // 初始化
        initJCQComponent()
        onEnable()
        JCQLog.info(Lang by "run.enable")
        enable = true
        return 0
    }

    /** 可重写的enable方法 */
    open fun onEnable() {}


    /**
     * 酷Q启动 (Type=[1001][IType.EVENT_Startup])<br></br>
     * 本方法会在酷Q【主线程】中被调用。<br></br>
     * 请在这里执行插件初始化代码。<br></br>
     * 请务必尽快返回本子程序，否则会卡住其他插件以及主程序的加载。
     *
     * @return 请固定返回0
     */
    final override fun startup(): Int {
        // 获取应用数据目录(无需储存数据时，请将此行注释)
        appDirectory = CQ.appDirectory
        // 系统类加载器与当前类加载器不同的时候，代表此时处于JCQ管控之下，则修改日志
        if (JCQApplication::class.java.classLoader != ClassLoader.getSystemClassLoader()) {
            QQLog.setDebugFunction { "${LocalDateTime.now()} [DEBUG ${Thread.currentThread().id}] [SIM-JCQ] $it" }
            QQLog.setInfoFunction { "${LocalDateTime.now()} [INFO  ${Thread.currentThread().id}] [SIM-JCQ] $it" }
            QQLog.setErrFunction { "${LocalDateTime.now()} [ERROR ${Thread.currentThread().id}] [SIM-JCQ] $it" }
            val warning: FortePlusPrintStream = QQLog.warning as FortePlusPrintStream
            val success: FortePlusPrintStream = QQLog.success as FortePlusPrintStream
            warning.setPrintFunction { "${LocalDateTime.now()} [WARN  ${Thread.currentThread().id}] [SIM-JCQ] $it" }
            success.setPrintFunction { "${LocalDateTime.now()} [SUCC  ${Thread.currentThread().id}] [SIM-JCQ] $it" }
        }
        JCQLog.info("酷Q已启动 当前Main ID: $ID")
        onStartUp()
        return 0
    }

    /** 可重写的 酷Q启动 */
    open fun onStartUp() {}

    /**
     * 应用将被停用 (Type=[1004][IType.EVENT_Disable])<br></br>
     * 当应用被停用前，将收到此事件。<br></br>
     * 如果酷Q载入时应用已被停用，则本函数【不会】被调用。<br></br>
     * 无论本应用是否被启用，酷Q关闭前本函数都【不会】被调用。
     *
     * @return 请固定返回0。
     */
    final override fun disable(): Int {
        onDisable()
        JCQLog.info(Lang by "run.disable")
        enable = false
        return 0
    }

    open fun onDisable() {}


    // *************************** EVENT ********************************* //


    /**
     * 私聊消息 (Type=[21][IType.EVENT_PrivateMsg])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType 子类型，11/来自好友 1/来自在线状态 2/来自群 3/来自讨论组
     * @param msgId   消息ID
     * @param fromQQ  来源QQ
     * @param msg     消息内容
     * @param font    字体
     * @return 返回值*不能*直接返回文本 如果要回复消息，请调用api发送<br></br>
     * 这里 返回  [com.sobte.cqp.jcq.entity.IMsg.MSG_INTERCEPT] - 截断本条消息，不再继续处理<br></br>
     * 注意：应用优先级设置为"最高"(10000)时，不得使用本返回值<br></br>
     * 如果不回复消息，交由之后的应用/过滤器处理，这里 返回  [MSG_IGNORE][com.sobte.cqp.jcq.entity.IMsg.MSG_IGNORE] - 忽略本条消息
     */
    override fun privateMsg(subType: Int, msgId: Int, fromQQ: Long, msg: String?, font: Int): Int = processor.onMsgSelected(JCQPrivateMsg(subType, msgId, fromQQ, msg, font)).toResult()

    /**
     * 群消息 (Type=[2][IType.EVENT_GroupMsg])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType       子类型，目前固定为1
     * @param msgId         消息ID
     * @param fromGroup     来源群号
     * @param fromQQ        来源QQ号
     * @param fromAnonymous 来源匿名者
     * @param msg           消息内容
     * @param font          字体
     * @return 关于返回值说明, 见 [私聊消息][privateMsg] 的方法
     */
    override fun groupMsg(subType: Int, msgId: Int, fromGroup: Long, fromQQ: Long, fromAnonymous: String?, msg: String?, font: Int): Int = processor.onMsgSelected(JCQGroupMsg(subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font)).toResult()

    /**
     * 讨论组消息 (Type=[4][IType.EVENT_DiscussMsg])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subtype     子类型，目前固定为1
     * @param msgId       消息ID
     * @param fromDiscuss 来源讨论组
     * @param fromQQ      来源QQ号
     * @param msg         消息内容
     * @param font        字体
     * @return 关于返回值说明, 见 [私聊消息][privateMsg] 的方法
     */
    override fun discussMsg(subType: Int, msgId: Int, fromDiscuss: Long, fromQQ: Long, msg: String?, font: Int): Int = processor.onMsgSelected(JCQDiscussMsg(subType, msgId, fromDiscuss, fromQQ, msg, font)).toResult()


    /**
     * 请求-好友添加 (Type=[301][IType.EVENT_Request_AddFriend])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType      子类型，目前固定为1
     * @param sendTime     发送时间(时间戳)
     * @param fromQQ       来源QQ
     * @param msg          附言
     * @param responseFlag 反馈标识(处理请求用)
     * @return 关于返回值说明, 见 [私聊消息][privateMsg] 的方法
     */
    override fun requestAddFriend(subType: Int, sendTime: Int, fromQQ: Long, msg: String?, responseFlag: String): Int = processor.onMsgSelected(JCQRequestAddFriend(subType, sendTime.toLong(), fromQQ, msg, responseFlag)).toResult()

    /**
     * 群文件上传事件 (Type=[11][IType.EVENT_GroupUpload])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType   子类型，目前固定为1
     * @param sendTime  发送时间(时间戳)// 10位时间戳
     * @param fromGroup 来源群号
     * @param fromQQ    来源QQ号
     * @param file      上传文件信息
     * @return 关于返回值说明, 见 [私聊消息][privateMsg] 的方法
     */
    override fun groupUpload(subType: Int, sendTime: Int, fromGroup: Long, fromQQ: Long, file: String): Int = processor.onMsgSelected(JCQGroupUpload(subType, sendTime.toLong(), fromGroup, fromQQ, file)).toResult()

    /**
     * 群事件-群成员减少 (Type=[102][IType.EVENT_System_GroupMemberDecrease])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType        子类型，1/群员离开 2/群员被踢
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param fromQQ         操作者QQ(仅子类型为2时存在)
     * @param beingOperateQQ 被操作QQ
     * @return 关于返回值说明, 见 [私聊消息][.privateMsg] 的方法
     */
    override fun groupMemberDecrease(subType: Int, sendTime: Int, fromGroup: Long, fromQQ: Long, beingOperateQQ: Long): Int = processor.onMsgSelected(JCQGroupMemberDecrease(subType, sendTime.toLong(), fromGroup, fromQQ, beingOperateQQ)).toResult()


    /**
     * 群事件-管理员变动 (Type=[101][IType.EVENT_System_GroupAdmin])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subtype        子类型，1/被取消管理员 2/被设置管理员
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param beingOperateQQ 被操作QQ
     * @return 关于返回值说明, 见 [私聊消息][.privateMsg] 的方法
     */
    override fun groupAdmin(subType: Int, sendTime: Int, fromGroup: Long, beingOperateQQ: Long): Int = processor.onMsgSelected(JCQGroupAdmin(subType, sendTime.toLong(), fromGroup, beingOperateQQ)).toResult()

    /**
     * 群事件-群成员增加 (Type=[103][IType.EVENT_System_GroupMemberIncrease])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType        子类型，1/管理员已同意 2/管理员邀请
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param fromQQ         操作者QQ(即管理员QQ)
     * @param beingOperateQQ 被操作QQ(即加群的QQ)
     * @return 关于返回值说明, 见 [私聊消息][.privateMsg] 的方法
     */
    override fun groupMemberIncrease(subType: Int, sendTime: Int, fromGroup: Long, fromQQ: Long, beingOperateQQ: Long): Int = processor.onMsgSelected(JCQGroupMemberIncrease(subType, sendTime.toLong(), fromGroup, fromQQ, beingOperateQQ)).toResult()

    /**
     * 请求-群添加 (Type=[302][IType.EVENT_Request_AddGroup])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType      子类型，1/他人申请入群 2/自己(即登录号)受邀入群
     * @param sendTime     发送时间(时间戳)
     * @param fromGroup    来源群号
     * @param fromQQ       来源QQ
     * @param msg          附言
     * @param responseFlag 反馈标识(处理请求用)
     * @return 关于返回值说明, 见 [私聊消息][.privateMsg] 的方法
     */
    override fun requestAddGroup(subType: Int, sendTime: Int, fromGroup: Long, fromQQ: Long, msg: String?, responseFlag: String): Int = processor.onMsgSelected(JCQRequestAddGroup(subType, sendTime.toLong(), fromGroup, fromQQ, msg, responseFlag)).toResult()

    /**
     * 好友事件-好友已添加 (Type=[201][IType.EVENT_Friend_Add])<br></br>
     * 本方法会在酷Q【线程】中被调用。<br></br>
     *
     * @param subType  子类型，目前固定为1
     * @param sendTime 发送时间(时间戳)
     * @param fromQQ   来源QQ
     * @return 关于返回值说明, 见 [私聊消息][.privateMsg] 的方法
     */
    override fun friendAdd(subType: Int, sendTime: Int, fromQQ: Long): Int = processor.onMsgSelected(JCQFriendAdd(subType, sendTime.toLong(), fromQQ)).toResult()
}