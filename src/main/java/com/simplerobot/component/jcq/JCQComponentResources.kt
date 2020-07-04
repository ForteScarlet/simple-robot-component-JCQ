/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-jcq (Code other than JCQ)
 * File     JCQComponentResources.kt (Code other than JCQ)
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 * JCQ's code is copyrighted by JCQ
 * you can see at: https://github.com/meowya/jcq-coolq
 *
 */

package com.simplerobot.component.jcq

import com.forte.lang.Language
import com.forte.qqrobot.*
import com.forte.qqrobot.beans.messages.result.*
import com.forte.qqrobot.beans.messages.types.GroupAddRequestType
import com.forte.qqrobot.bot.BotInfoImpl
import com.forte.qqrobot.bot.BotManager
import com.forte.qqrobot.bot.BotSender
import com.forte.qqrobot.bot.LoginInfo
import com.forte.qqrobot.depend.DependCenter
import com.forte.qqrobot.sender.MsgSender
import com.forte.qqrobot.sender.senderlist.BaseRootSenderList
import com.forte.qqrobot.utils.CQCodeUtil
import org.meowy.cqp.jcq.entity.*

/*
    扩展函数
 */
/**
 * 字符串或者或者为null
 */
fun String?.orNull() = this ?: "null"

/**
 * 将发送消息的返回值转化为ID字符串，如果为负数则会抛出异常
 */
private fun Int.toResultID(): String = if (this < 0) {
    val status = CQStatus.getStatus(this)
    throw JCQSenderException("failed", status.id, status.msg)
} else {
    this.toString()
}

/**
 * 将发送消息的返回值转化为ID字符串，如果为负数则会抛出异常
 */
private fun Int.toResultBool(): Boolean = if (this < 0) {
    throw JCQSenderException("failed", this.toString())
} else {
    true
}

/**
 * 获取不支持API的本地化语言
 */
fun noSupportApiName(name: String): String = Language.format("api.not_support", name)

/**
 * JCQ 相关配置类
 * 好像..没啥好额外配置的
 */
open class JCQConfiguration : BaseConfiguration<JCQConfiguration>() {
    init {
        // 初始化，将线程池配置修改为1 / 1
        // 因此默认情况下，线程池的数量为1/1
        corePoolSize = 1
        maximumPoolSize = 1
        this.registerBot("", "")
    }
}

/**
 * JCQ 启动接口
 */
interface JCQBotApp : Application<JCQConfiguration>

object JCQBotAppImpl : JCQBotApp {
    override fun before(configuration: JCQConfiguration?) {}

    override fun after(cqCodeUtil: CQCodeUtil?, sender: MsgSender?) {}
}


/**
 * JCQ context对象
 */
open class JCQContext
@JvmOverloads
constructor(
        sender: JCQSender,
        manager: BotManager,
        msgParser: MsgParser,
        processor: MsgProcessor,
        dependCenter: DependCenter,
        configuration: JCQConfiguration,
        application: JCQApplication,
        // CoolQ对象
        val cq: CoolQ
) : SimpleRobotContext<JCQSender, JCQSender, JCQSender, JCQConfiguration, JCQApplication>(sender, sender, sender, manager, msgParser, processor, dependCenter, configuration, application)


/**
 * JCQBotInfo
 */
open class JCQBotInfo(info: LoginInfo, botSender: BotSender, cq: CoolQ) : BotInfoImpl(info.code, cq.appDirectory, info, botSender)


/**
 * send送信器
 * 需要一个cq参数
 * CQ全局唯一，这个类基本上也可以算是全局唯一了
 */
open class JCQSender(val cq: CoolQ) : BaseRootSenderList() {



    // 登录信息不再使用静态
        /** 登录信息 */
        private lateinit var _loginInfo: LoginInfo
        val loginInfo: LoginInfo
            get() {
                if (!::_loginInfo.isInitialized) {
                    _loginInfo = JCQLoginInfo(cq.loginNick, cq.loginQQ)
                }
                return _loginInfo
            }

        /** app目录 */
        private lateinit var _appDirectory: String
        val appDir: String
            get() {
                if (!::_appDirectory.isInitialized) {
                    _appDirectory = cq.appDirectory
                }
                return _appDirectory
            }

    /**
     * 发送讨论组消息
     * @param group 群号
     * @param msg   消息内容
     * @return 失败返回负值, 成功返回消息ID
     */
    override fun sendDiscussMsg(group: String, msg: String?): String = cq.sendDiscussMsg(group.toLong(), msg.orNull()).toResultID()


    /**
     * 发送群消息
     * @param group 群号
     * @param msg   消息内容
     */
    override fun sendGroupMsg(group: String, msg: String?): String = cq.sendGroupMsg(group.toLong(), msg.orNull()).toResultID()

    /**
     * 发送私聊信息
     * @param QQ    QQ号
     * @param msg   消息内容
     */
    override fun sendPrivateMsg(QQ: String, msg: String?): String = cq.sendPrivateMsg(QQ.toLong(), msg.orNull()).toResultID()

    /**
     * 发送名片赞
     * @param QQ    QQ号
     * @param times 次数
     */
    override fun sendLike(QQ: String, times: Int): Boolean = cq.sendLike(QQ.toLong(), times).toResultBool()


    /**
     * 设置匿名成员禁言
     * @param group 群号
     * @param flag  匿名成员标识
     * @param time  时长，一般是以分钟为单位
     */
    override fun setGroupAnonymousBan(group: String, flag: String, time: Long): Boolean = cq.setGroupAnonymousBan(group.toLong(), flag, time).toResultBool()

    /**
     * 踢出群成员
     * @param group 群号
     * @param QQ    QQ号
     * @param dontBack  是否拒绝再次申请
     */
    override fun setGroupMemberKick(group: String, QQ: String, dontBack: Boolean): Boolean = cq.setGroupKick(group.toLong(), QQ.toLong(), dontBack).toResultBool()

    /**
     * 退出讨论组
     * @param group 讨论组号
     */
    override fun setDiscussLeave(group: String): Boolean = cq.setDiscussLeave(group.toLong()).toResultBool()

    /**
     * 设置群管理员
     * @param group 群号
     * @param QQ    qq号
     * @param set   是否设置为管理员
     */
    override fun setGroupAdmin(group: String, QQ: String, set: Boolean): Boolean = cq.setGroupAdmin(group.toLong(), QQ.toLong(), set).toResultBool()

    /**
     * 是否允许群匿名聊天
     * @param group 群号
     * @param agree 是否允许
     */
    override fun setGroupAnonymous(group: String, agree: Boolean): Boolean = cq.setGroupAnonymous(group.toLong(), agree).toResultBool()


    // 不支持： 打卡
//        override fun setSign(): Boolean = super.setSign()

    /**
     * 好友请求申请
     * @param flag  一般会有个标识
     * @param friendName    如果通过，则此参数为好友备注
     * @param agree 是否通过
     */
    override fun setFriendAddRequest(flag: String, friendName: String?, agree: Boolean): Boolean {
        val backType = if (agree) {
            IRequest.REQUEST_ADOPT
        } else {
            IRequest.REQUEST_REFUSE
        }
        return cq.setFriendAddRequest(flag, backType, friendName.orEmpty()).toResultBool()
    }


    /**
     * 消息撤回 似乎只需要一个消息ID即可
     * 需要pro
     * @param flag  消息标识
     */
    override fun setMsgRecall(flag: String): Boolean = cq.deleteMsg(flag.toLong()).toResultBool()

    /**
     * 设置群成员名片
     * @param group 群号
     * @param QQ    QQ号
     * @param card  名片
     */
    override fun setGroupCard(group: String, QQ: String, card: String?): Boolean = cq.setGroupCard(group.toLong(), QQ.toLong(), card.orEmpty()).toResultBool()

    /**
     * 设置全群禁言
     * @param group 群号
     * @param in    是否开启全群禁言
     */
    override fun setGroupWholeBan(group: String, `in`: Boolean): Boolean = cq.setGroupWholeBan(group.toLong(), `in`).toResultBool()

    /**
     * 群添加申请
     * @param flag  一般会有个标识
     * @param requestType   加群类型  邀请/普通添加
     * @param agree 是否同意
     * @param why   如果拒绝，则此处为拒绝理由
     */
    override fun setGroupAddRequest(flag: String, requestType: GroupAddRequestType, agree: Boolean, why: String?): Boolean {
        /*
         * @param requestType  根据请求事件的子类型区分 REQUEST_GROUP_ADD(群添加) 或 REQUEST_GROUP_INVITE(群邀请)
         * @param backType     REQUEST_ADOPT(通过) 或 REQUEST_REFUSE(拒绝)
         */
        val requestTypeId = if (requestType == GroupAddRequestType.ADD) {
            IRequest.REQUEST_GROUP_ADD
        } else {
            IRequest.REQUEST_GROUP_INVITE
        }
        val backType = if (agree) {
            IRequest.REQUEST_ADOPT
        } else {
            IRequest.REQUEST_REFUSE
        }
        val groupAddRequestResult = cq.setGroupAddRequest(flag, requestTypeId, backType, why.orEmpty())
        return groupAddRequestResult.toResultBool()
    }

    /**
     * 退出群
     * @param group 群号
     * @param dissolve 假如此账号是群主，则此参数代表是否要解散群。默认为false
     */
    override fun setGroupLeave(group: String, dissolve: Boolean): Boolean = cq.setGroupLeave(group.toLong(), dissolve).toResultBool()


    /**
     * 设置群成员专属头衔
     * @param group 群号
     * @param QQ    QQ号
     * @param title 头衔
     * @param time  有效时长，一般为分钟吧
     */
    override fun setGroupExclusiveTitle(group: String, QQ: String, title: String?, time: Long): Boolean = cq.setGroupSpecialTitle(group.toLong(), QQ.toLong(), title.orEmpty(), time).toResultBool()

    /**
     * 设置群禁言
     * @param group 群号
     * @param QQ    QQ号
     * @param time  时长，一般是以秒为单位
     */
    override fun setGroupBan(group: String, QQ: String, time: Long): Boolean = cq.setGroupBan(group.toLong(), QQ.toLong(), time).toResultBool()

    /**
     * 置错误提示
     */
    fun setFatal(errorInfo: String): Int = cq.setFatal(errorInfo)


    // ****************** getters ******************** //


    /**
     * 取群成员列表
     * @param group 群号
     * @return  成员列表
     */
    override fun getGroupMemberList(group: String): GroupMemberList = JCQGroupMemberList(cq.getGroupMemberList(group.toLong()))


    /**
     * 获取登录的QQ的信息
     * @return 登录QQ的信息
     */
    override fun getLoginQQInfo(): LoginInfo = loginInfo

    /**
     * 获取权限信息
     * 一般不需要参数
     * @return 权限信息
     */
    override fun getAuthInfo(): AuthInfo = JCQAuthInfo(cq.cookies, cq.csrfToken)


    /**
     * 取匿名成员信息
     * 一般是使用匿名标识来获取
     * @return 匿名成员信息
     */
    override fun getAnonInfo(flag: String): AnonInfo = JCQAnonInfo(cq.getAnonymous(flag))

    /**
     * 接收消息中的语音(record)
     * @param file      收到消息中的语音文件名(file)
     * @param outFormat 应用所需的语音文件格式，目前支持 mp3,amr,wma,m4a,spx,ogg,wav,flac
     * @return 返回保存在 \data\record\ 目录下的文件名
     */
    fun getRecord(file: String, outFormat: String): String = cq.getRecord(file, outFormat)

//        /**
//         * 取群信息
//         * @param group 群号
//         * @param cache 是否使用缓存
//         * @return 群信息
//         */
//        override fun getGroupInfo(group: String?, cache: Boolean): GroupInfo {
//        }

    /**
     * 取群列表
     * @return 群列表
     */
    override fun getGroupList(): GroupList = JCQGroupList(cq.groupList)


    /**
     * 取陌生人信息
     * @param QQ 陌生人的QQ号
     * @param cache 是否使用缓存
     * @return
     */
    override fun getStrangerInfo(QQ: String, cache: Boolean): StrangerInfo? {
        val info: QQInfo? = cq.getStrangerInfo(QQ.toLong(), cache)
        return if (info == null) {
            null
        } else {
            JCQStrangerInfo(info)
        }
    }

    /**
     * 取群成员信息
     * @param group 群号
     * @param QQ    QQ号
     * @param cache 是否使用缓存
     * @return 群成员信息
     */
    override fun getGroupMemberInfo(group: String, QQ: String, cache: Boolean): GroupMemberInfo? {
        val groupMember: Member? = cq.getGroupMemberInfo(group.toLong(), QQ.toLong(), cache)
        return if (groupMember == null) {
            null
        } else {
            JCQGroupMemberInfo(groupMember)
        }
    }

    /**
     * 获取群文件信息
     * @param flag 文件标识
     * @return 群文件信息
     */
    override fun getFileInfo(flag: String): FileInfo? {
        val groupFile: GroupFile? = cq.getGroupFile(flag)
        return if (groupFile == null) {
            null
        } else {
            JCQGroupFile(groupFile)
        }
    }

    /**
     * 获取最后的状态
     */
    val lastStatus: CQStatus get() = cq.lastStatus

    /**
     * 获取app文件路径
     */
    val appDirectory: String get() = appDir

    /**
     * 获取字体
     * @param font 字体
     * @return 字体信息
     */
    open fun getFont(font: Int): Font = cq.getFont(font)

    /**
     * 输出日志
     * @see [CoolQ.logDebug]
     */
    fun logDebug(category: String?, content: String?) = cq.logDebug(category, content)

    /**
     * 输出日志
     * @see [CoolQ.logError]
     */
    fun logError(category: String?, content: String?) = cq.logError(category, content)

    /**
     * 输出日志
     * @see [CoolQ.logFatal]
     */
    fun logFatal(category: String?, content: String?) = cq.logFatal(category, content)

    /**
     * 输出日志
     * @see [CoolQ.logInfo]
     */
    fun logInfo(category: String?, content: String?) = cq.logInfo(category, content)


    /**
     * 输出日志
     * @see [CoolQ.logInfoSend]
     */
    fun logInfoSend(category: String?, content: String?) = cq.logInfoSend(category, content)

    /**
     * 输出日志
     * @see [CoolQ.logInfoRecv]
     */
    fun logInfoRecv(category: String?, content: String?) = cq.logInfoRecv(category, content)

    /**
     * 输出日志
     * @see [CoolQ.logInfoSuccess]
     */
    fun logInfoSuccess(category: String?, content: String?) = cq.logInfoSuccess(category, content)

    /**
     * 输出日志
     * @see [CoolQ.logWarning]
     */
    fun logWarning(category: String?, content: String?) = cq.logWarning(category, content)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JCQSender

        if (cq != other.cq) return false

        return true
    }

    override fun hashCode(): Int {
        return cq.hashCode()
    }

}











