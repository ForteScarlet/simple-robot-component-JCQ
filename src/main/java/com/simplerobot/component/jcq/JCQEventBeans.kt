package com.simplerobot.component.jcq

import com.forte.qqrobot.beans.messages.OriginalAble
import com.forte.qqrobot.beans.messages.msgget.*
import com.forte.qqrobot.beans.messages.types.*
import com.sobte.cqp.jcq.entity.GroupFile
import com.sobte.cqp.jcq.event.JcqApp

/** 参数拼接为originalData */
private fun toOriginal(name: String, vararg data: String?): String = "$name: ${data.joinToString(", ")}"

/** 参数拼接为originalData */
private fun toOriginal(name: String, vararg data: Pair<String, Any?>): String = "$name: ${data.map { "${it.first}=${it.second}" }.joinToString(", ")}"

/**
 * Event继承抽象类，默认实现了一些公共方法
 */
abstract class BaseJCQMsg(private val original: String) : OriginalAble {
    constructor(name: String, vararg data: Pair<String, Any?>) : this(toOriginal(name, *data))
    constructor(name: String, vararg data: String?) : this(toOriginal(name, *data))

    /** 接收到消息的时候的毫秒值 */
    protected open val onTime = System.currentTimeMillis()

    /** 获取原本的数据 originalData  */
    override fun getOriginalData(): String = original

    /** to String */
    override fun toString(): String = originalData

    /**
     * 允许重新定义Code以实现在存在多个机器人的时候切换处理。
     * @param code code
     */
    @Deprecated("meaningless")
    open fun setThisCode(code: String) {
    }

    /**
     * 此消息获取的时候，代表的是哪个账号获取到的消息。
     * @return 接收到此消息的账号。
     */
    @Deprecated("meaningless", ReplaceWith("JCQSender.loginInfo.code"))
    open fun getThisCode(): String = JCQSender.loginInfo.code

    /** 获取到的时间, 代表某一时间的秒值。一般情况下是秒值。如果类型不对请自行转化  */
    open fun getTime(): Long = onTime
}

/**
 * JCQ 私信消息事件
 */
open class JCQPrivateMsg(val subType: Int, val msgId: Int,
                         val fromQQ: Long, private var onMsg: String?,
                         val font: Int) :
        BaseJCQMsg("JCQPrivateMsg",
                "subType" to subType, "msgId" to msgId, "fromQQ" to fromQQ, "msg" to onMsg, "font" to font), PrivateMsg {

    // 子类型，11/来自好友 1/来自在线状态 2/来自群 3/来自讨论组
    private val msgType: PrivateMsgType = when (subType) {
        11 -> PrivateMsgType.FROM_FRIEND
        1 -> PrivateMsgType.FROM_ONLINE
        2 -> PrivateMsgType.FROM_GROUP
        3 -> PrivateMsgType.FROM_DISCUSS
        else -> PrivateMsgType.FROM_SYSTEM
    }

    /** 获取发送人的QQ号  */
    override fun getQQ(): String = fromQQ.toString()

    /** 获取原本的数据 originalData  */
    override fun getOriginalData(): String = toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = msgId.toString()

    /** 获取私聊消息类型  */
    override fun getType(): PrivateMsgType = msgType

    /**
     * 重新设置消息
     * @param newMsg msg
     * @since 1.7.x
     */
    override fun setMsg(newMsg: String?) {
        onMsg = newMsg
    }

    /** 获取消息的字体  */
    override fun getFont(): String = font.toString()

    /**
     * 一般来讲，监听到的消息大部分都会有个“消息内容”。定义此方法获取消息内容。
     * 如果不存在，则为null。（旧版本推荐为空字符串，现在不了。我变卦了）
     */
    override fun getMsg(): String? = onMsg

}

/**
 * 群消息
 */
open class JCQGroupMsg(
        subType: Int, val msgId: Int, val fromGroup: Long,
        val fromQQ: Long, val fromAnonymous: String?, private var onMsg: String?,
        val font: Int
) :
        BaseJCQMsg("JCQGroupMsg",
                "subType" to subType, "msgId" to msgId, "fromGroup" to fromGroup,
                "fromQQ" to fromQQ, "fromAnonymous" to fromAnonymous, "msg" to onMsg, "font" to font),
        GroupMsg {
    /** 获取群消息发送人的qq号  */
    override fun getQQ(): String = fromQQ.toString()

    /** 获取群消息的群号  */
    override fun getGroup(): String = fromGroup.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = msgId.toString()

    private var _powerType = PowerType.MEMBER

    /**
     * 获取此人在群里的权限
     * @return 权限，例如群员、管理员等
     */
    @Deprecated("No such attribute")
    override fun getPowerType(): PowerType = _powerType

    /**
     * 重新定义此人的权限
     * @param powerType 权限
     */
    override fun setPowerType(powerType: PowerType) {
        _powerType = powerType
    }

    /**
     * 一般来讲，监听到的消息大部分都会有个“消息内容”。定义此方法获取消息内容。
     * 如果不存在，则为null。（旧版本推荐为空字符串，现在不了。我变卦了）
     */
    override fun getMsg(): String? = onMsg


    /** 获取消息类型
     *  subType  子类型，目前固定为1，即普通消息
     * */
    override fun getType(): GroupMsgType = GroupMsgType.NORMAL_MSG

    /**
     * 重新设置消息
     * @param newMsg msg
     * @since 1.7.x
     */
    override fun setMsg(newMsg: String?) {
        onMsg = newMsg
    }

    /** 获取消息的字体  */
    override fun getFont(): String = font.toString()

}

/**
 * 讨论组消息
 */
open class JCQDiscussMsg(
        subType: Int, val msgId: Int, val fromDiscuss: Long, val fromQQ: Long,
        private var onMsg: String?, val font: Int
) :
        BaseJCQMsg("JCQDiscussMsg",
                "subType" to subType, "msgId" to msgId, "fromDiscuss" to fromDiscuss,
                "fromQQ" to fromQQ, "msg" to onMsg, "font" to font),
        DiscussMsg {
    /** 获取发消息的人的QQ  */
    override fun getQQ(): String = fromQQ.toString()

    /** 获取讨论组号  */
    override fun getGroup(): String = fromDiscuss.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = msgId.toString()

    /**
     * 重新设置消息
     * @param newMsg msg
     * @since 1.7.x
     */
    override fun setMsg(newMsg: String?) {
        onMsg = newMsg
    }

    /** 获取消息的字体  */
    override fun getFont(): String = font.toString()

    /**
     * 一般来讲，监听到的消息大部分都会有个“消息内容”。定义此方法获取消息内容。
     * 如果不存在，则为null。（旧版本推荐为空字符串，现在不了。我变卦了）
     */
    override fun getMsg(): String? = onMsg
}

open class JCQRequestAddFriend(
        subType: Int, override val onTime: Long, val fromQQ: Long,
        private var onMsg: String?, val responseFlag: String
) :
        BaseJCQMsg("JCQRequestAddFriend",
                "subType" to subType, "sendTime" to onTime,
                "fromQQ" to fromQQ, "msg" to onMsg, "responseFlag" to responseFlag),
        FriendAddRequest {
    /** 请求人QQ  */
    override fun getQQ(): String = fromQQ.toString()

    /** 获取ID, 即标识 */
    override fun getId(): String = responseFlag

    /** 获取标识  */
    override fun getFlag(): String = responseFlag

    /** 请求消息  */
    override fun getMsg(): String? = onMsg

    override fun setMsg(msg: String?) {
        onMsg = msg
    }
}

open class JCQGroupUpload(
        val subType: Int, override val onTime: Long, val fromGroup: Long,
        val fromQQ: Long, val file: String
) :
        BaseJCQMsg("JCQGroupUpload",
                "subType" to subType, "sendTime" to onTime,
                "fromGroup" to fromGroup, "fromQQ" to fromQQ,
                "file" to file),
        GroupFileUpload {

    /** 延迟初始化的幕后文件信息 */
    private lateinit var _fileInfo: GroupFile
    /** 文件信息 */
    private val fileInfo: GroupFile
        get() {
            // 还没有初始化
            if (!::_fileInfo.isInitialized) {
                _fileInfo = JcqApp.CQ.getGroupFile(file)
            }
            return _fileInfo
        }

    /** 发信人QQ  */
    override fun getQQ(): String = fromQQ.toString()

    /** 群号  */
    override fun getGroup(): String = fromGroup.toString()


    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = "$onTime.$subType.$file"

    /** 文件名  */
    override fun getFileName(): String = fileInfo.name

    /** 文件大小 Long类型，字节  */
    override fun getFileSize(): Long = fileInfo.size

    /** 获取文件Busid  */
    override fun getFileBusid(): String = fileInfo.busid.toString()

}

/**
 * 群成员减少事件
 */
open class JCQGroupMemberDecrease(
        val subType: Int, override val onTime: Long, val fromGroup: Long,
        val fromQQ: Long, val beingOperateQQ: Long
) :
        BaseJCQMsg("JCQGroupUpload",
                "subType" to subType, "sendTime" to onTime,
                "fromGroup" to fromGroup, "fromQQ" to fromQQ,
                "beingOperateQQ" to beingOperateQQ),
        GroupMemberReduce {

    /** 子类型，1/群员离开 2/群员被踢 */
    private val decreaseType: ReduceType = when (subType) {
        1 -> ReduceType.LEAVE
        else -> ReduceType.KICK_OUT
    }

    /** 被操作者的QQ号  */
    override fun getBeOperatedQQ(): String = beingOperateQQ.toString()

    /** 群号  */
    override fun getGroup(): String = fromGroup.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = "$subType.$onTime.$beingOperateQQ"

    /** 操作者的QQ号  */
    override fun getOperatorQQ(): String = fromQQ.toString()

    /** 获取类型  */
    override fun getType(): ReduceType = decreaseType
}

/**
 * 群事件-管理员变动
 */
open class JCQGroupAdmin(
        val subType: Int, override val onTime: Long,
        val fromGroup: Long, val beingOperateQQ: Long
) :
        BaseJCQMsg("JCQGroupAdmin",
                "subType" to subType, "sendTime" to onTime,
                "fromGroup" to fromGroup, "beingOperateQQ" to beingOperateQQ),
        GroupAdminChange {

    /** 子类型，1/被取消管理员 2/被设置管理员 */
    private val changeType: GroupAdminChangeType = when (subType) {
        1 -> GroupAdminChangeType.CANCEL_ADMIN
        else -> GroupAdminChangeType.BECOME_ADMIN
    }

    /** 被操作者的QQ号  */
    override fun getBeOperatedQQ(): String = beingOperateQQ.toString()

    /** 来自的群  */
    override fun getGroup(): String = fromGroup.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = "$subType.$onTime.$beingOperateQQ"

    /** 操作者的QQ号, 肯定就是群主，但是CQ没有提供相关方法与参数，暂时留null  */
    @Deprecated("No such data", ReplaceWith("null"))
    override fun getOperatorQQ(): String? = null

    /** 获取管理员变动类型  */
    override fun getType(): GroupAdminChangeType = changeType

}

/**
 * 群事件-群成员增加
 */
open class JCQGroupMemberIncrease(
        val subType: Int, override val onTime: Long, val fromGroup: Long,
        val fromQQ: Long, val beingOperateQQ: Long
) :
        BaseJCQMsg("JCQGroupMemberIncrease",
                "subType" to subType, "sendTime" to onTime,
                "fromGroup" to fromGroup, "beingOperateQQ" to beingOperateQQ),
        GroupMemberIncrease {

    // 子类型，1/管理员已同意 2/管理员邀请
    private val type: IncreaseType = when (subType) {
        1 -> IncreaseType.AGREE
        else -> IncreaseType.INVITE
    }

    /** 被操作者的QQ号  */
    override fun getBeOperatedQQ(): String = beingOperateQQ.toString()

    /** 群号  */
    override fun getGroup(): String = fromGroup.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = "$subType.$onTime.$beingOperateQQ"

    /** 操作者的QQ号  */
    override fun getOperatorQQ(): String = fromQQ.toString()

    /** 获取类型  */
    override fun getType(): IncreaseType = type
}

/**
 * 请求-群添加
 */
open class JCQRequestAddGroup(
        val subType: Int, override val onTime: Long, val fromGroup: Long,
        val fromQQ: Long, private val onMsg: String?, val responseFlag: String
) :
        BaseJCQMsg("JCQRequestAddGroup",
                "subType" to subType, "sendTime" to onTime,
                "fromGroup" to fromGroup, "fromQQ" to fromQQ, "msg" to onMsg, "responseFlag" to responseFlag),
        GroupAddRequest {

    /** 子类型，1/他人申请入群 2/自己(即登录号)受邀入群 */
    private val type: GroupAddRequestType = when (subType) {
        1 -> GroupAddRequestType.ADD
        else -> GroupAddRequestType.INVITE
    }

    /** 获取QQ号  */
    override fun getQQ(): String = fromQQ.toString()

    /** 获取群号  */
    override fun getGroup(): String = fromGroup.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = "$subType.$onTime.$responseFlag"

    /** 获取消息  */
    override fun getMsg(): String? = onMsg

    /** 加群类型  */
    override fun getRequestType(): GroupAddRequestType = type

    /** 获取标识  */
    override fun getFlag(): String = responseFlag
}


open class JCQFriendAdd(
        val subType: Int, override val onTime: Long, val fromQQ: Long
) :
        BaseJCQMsg("JCQFriendAdd",
                "subType" to subType,
                "sendTime" to onTime, "fromQQ" to fromQQ),
        FriendAdd {
    /** 添加人的QQ  */
    override fun getQQ(): String = fromQQ.toString()

    /** 获取ID, 一般用于消息类型判断  */
    override fun getId(): String = "$subType.$onTime.$fromQQ"

}
