package com.simplerobot.component.jcq

import com.forte.qqrobot.beans.messages.OriginalAble
import com.forte.qqrobot.beans.messages.result.*
import com.forte.qqrobot.beans.messages.result.inner.GroupMember
import com.forte.qqrobot.beans.messages.types.PowerType
import com.forte.qqrobot.beans.messages.types.SexType
import com.forte.qqrobot.bot.LoginInfo
import org.meowy.cqp.jcq.entity.*
import org.meowy.cqp.jcq.entity.enumerate.Authority
import org.meowy.cqp.jcq.entity.enumerate.Gender
import com.forte.qqrobot.beans.messages.result.inner.Group as SimGroup

/*
    此文件构建通过Getter得到的beans
 */

/**
 * 权限信息，一般都是1（或其他）/成员 2/管理员 3/群主
 */
private fun Authority.toPowerType() = when (this) {
    Authority.ADMIN -> PowerType.ADMIN
    Authority.OWNER -> PowerType.OWNER
    else -> PowerType.MEMBER
}

/**
 * 性别信息，一般都是1/成员 2/管理员 3/群主
 */
private fun Gender.toSexType() = when (this) {
    Gender.MALE -> SexType.MALE
    Gender.FEMALE -> SexType.FEMALE
    else -> SexType.UNKNOWN
}


abstract class JCQBaseBean(
        @get:JvmName("__getOriginalData__")
        var originalData: String
) : OriginalAble {
    override fun getOriginalData() = originalData
}

// ************************** group member list ********************************** //

/**
 * 群成员列表
 */
data class JCQGroupMemberList(val memberList: List<Member>) :
        JCQBaseBean(memberList.toString()), GroupMemberList {

    @get:JvmName("__getList__")
    val list: Array<GroupMember> = memberList.map { JCQGroupMember(it) as GroupMember }.toTypedArray()

    /**
     * 获取列表
     */
    override fun getList(): Array<GroupMember> = list
}


/**
 * 群成员
 */
data class JCQGroupMember(private val mi: Member) : JCQBaseBean(mi.toString()), GroupMember {

    /** 性别 */
    private val sexType = mi.gender.toSexType()

    /**
     * 管理权限 1/成员 2/管理员 3/群主
     * private int Authority;
     */
    private val powerType = mi.authority.toPowerType()

    /** 获取专属头衔  */
    override fun getExTitle() = mi.title

    /** QQ号  */
    override fun getQQ() = mi.qqId.toString()

    /** 群号  */
    override fun getGroup() = mi.groupId.toString()

    /** QQ名  */
    override fun getName() = mi.nick

    /** 等级对应名称  */
    override fun getLevelName() = mi.levelName

    /** 加群时间, 毫秒值  */
    override fun getJoinTime() = mi.addTime.time

    /** 获取性别  */
    override fun getSex(): SexType = sexType

    /** 权限类型  */
    override fun getPower(): PowerType = powerType

    /** 头衔到期时间  */
    override fun getExTitleTime(): Long = mi.titleExpire?.time ?: -1

    /** 是否允许修改群名片  */
    override fun isAllowChangeNick(): Boolean = mi.isModifyCard

    /** 最后发言时间  */
    override fun getLastTime(): Long = mi.lastTime.time

    /** 是否为不良用户  */
    override fun isBlack(): Boolean = mi.isBad

    /** 获取群昵称  */
    override fun getNickName(): String = mi.nick

    /** 所在城市  */
    override fun getCity(): String = mi.area

    /** 头像  */
    override fun getHeadUrl(): String = qqHeadUrl
}


// ******************************* login info ********************************** //

/**
 * 账号信息
 */
data class JCQLoginInfo
@JvmOverloads
constructor(var nick: String, var userCode: String, @get:JvmName("__getLevel__") var level: Int = -1) :
        JCQBaseBean("nick=$nick,code=$userCode,level=$level"), LoginInfo {
    constructor(nick: String, userCode: Long, level: Int = -1) : this(nick, userCode.toString(), level)

    /** QQ号 */
    override fun getQQ() = userCode

    /** 昵称 */
    override fun getName() = nick

    /** 等级 */
    override fun getLevel() = level
}

// ******************************* auth info ********************************** //

/**
 * 权限信息
 */
data class JCQAuthInfo
@JvmOverloads
constructor(
        var jCookies: String,
        var jCsrfToken: String,
        var jCode: String? = null
) :
        JCQBaseBean("cookies=$jCookies,csrfToken=$jCsrfToken,code=$jCode"), AuthInfo {
    constructor(cookies: String, csrfToken: Int, code: String? = null) : this(cookies, csrfToken.toString(), code)


    /**
     * 获取cookies信息
     */
    override fun getCookies() = jCookies

    /**
     * 获取一个编码
     */
    override fun getCode() = jCode

    /**
     * 获取CsrfToken
     */
    override fun getCsrfToken() = jCsrfToken
}

// ******************************* anon info ********************************** //

data class JCQAnonInfo(private val info: Anonymous) :
        JCQBaseBean(info.toString()), AnonInfo {

    val byteToken = info.token

    /**
     * 获取ID
     */
    override fun getId(): String = info.aid.toString()

    /**
     * Token, 原数据似乎是数据流形式
     */
    override fun token(): String = String(info.token)

    /**
     * 获取匿名名称
     */
    override fun getAnonName() = info.name
}

// ******************************* group list ********************************** //

/**
 * 群列表
 */
data class JCQGroupList(private val list: List<Group>) : JCQBaseBean(list.toString()), GroupList {
    /**
     * 获取列表
     */
    override fun getList(): Array<SimGroup> = list.map { JCQGroupInfo(it) as SimGroup }.toTypedArray()
}

/**
 * 群列表中的群消息
 */
data class JCQGroupInfo(private val gi: Group) :
        JCQBaseBean(gi.toString()), SimGroup {

    /** 群名  */
    override fun getName() = gi.name

    /** 群号  */
    override fun getCode() = gi.id.toString()
}

// ******************************* stranger info ********************************** //

/**
 * 陌生人信息
 */
data class JCQStrangerInfo(private val info: QQInfo) :
        JCQBaseBean(info.toString()), StrangerInfo {

    /** 性别 0/男性 1/女性 */
    private val sexType = info.gender.toSexType()

    /** QQ号  */
    override fun getQQ(): String = info.qqId.toString()

    /** 获取名称（昵称）  */
    override fun getName(): String = info.nick

    /** 年龄  */
    override fun getAge(): Int = info.age

    /** 性别  */
    override fun getSex(): SexType = sexType

    /** 等级  */
    override fun getLevel(): Int = -1
}

// ******************************* group member info ********************************** //

/**
 * JCQ群成员信息
 */
data class JCQGroupMemberInfo(private val gm: Member) :
        JCQBaseBean(gm.toString()), GroupMemberInfo {

    private val powerInfo: PowerType = gm.authority.toPowerType()
    private val sexInfo: SexType = gm.gender.toSexType()

    /** 获取专属头衔  */
    override fun getExTitle(): String = gm.title

    /** 成员QQ号  */
    override fun getQQ(): String = gm.qqId.toString()

    /** 群名片  */
    override fun getCard(): String = gm.card

    /** qq昵称  */
    override fun getName(): String = gm.nick

    /** 权限类型  */
    override fun getPowerType(): PowerType = powerInfo

    /** 群成员等级名称  */
    override fun getLevelName(): String = gm.levelName

    /** 加群时间  */
    override fun getJoinTime(): Long = gm.addTime.time

    /** 头像地址  */
    override fun getHeadImgUrl(): String = qqHeadUrl

    /** 获取性别  */
    override fun getSex(): SexType = sexInfo

    /** 头衔的有效期  */
    override fun getExTitleTime(): Long = gm.titleExpire?.time ?: -1

    /** 是否允许修改群昵称  */
    override fun isAllowChangeNick(): Boolean = gm.isModifyCard

    /** 最后一次发言时间  */
    override fun getLastTime(): Long = gm.lastTime.time

    /** 禁言剩余时间  */
    @Deprecated("This data does not exist", ReplaceWith("-1"))
    override fun getBanTime(): Long = -1

    /** 是否为不良用户  */
    override fun isBlack(): Boolean = gm.isBad

    /** 群昵称  */
    override fun getNickName(): String = gm.card

    /** 获取群号  */
    override fun getCode(): String = gm.groupId.toString()

    /** 所在城市  */
    override fun getCity(): String = gm.area
}

// ******************************* group file ********************************** //

/**
 * 群文件
 */
data class JCQGroupFile(private val info: GroupFile) :
        JCQBaseBean(info.toString()), FileInfo {
    /** 获取文件名称  */
    override fun getFileName(): String = info.name

    /** 文件BUSID  */
    override fun getBusid(): String = info.busid.toString()

    /** 文件ID  */
    override fun getId(): String = info.id

    /** 获取文件大小-字节  */
    override fun getFileSize(): Long = info.size
}

















