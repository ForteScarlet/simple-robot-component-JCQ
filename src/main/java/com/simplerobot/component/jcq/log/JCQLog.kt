package com.simplerobot.component.jcq.log

import com.forte.lang.Language
import com.simplerobot.component.jcq.JCQMain
import com.sobte.cqp.jcq.entity.CoolQ
import com.sobte.cqp.jcq.event.JcqApp
import sun.reflect.Reflection


/**
 * 与[com.forte.qqrobot.log.QQLog]不同，此类为输出至酷Q日志的日志类
 */
object JCQLog {
    private val cq: CoolQ get() = JcqApp.CQ
    private val position: Class<*>
        get() = try {
            // Ref -> pos -> info -> real
            // 0      1      2       3
            Reflection.getCallerClass(3)
        } catch (e: Throwable) {
            JCQMain::class.java
        }
    private const val category = "[SIM-JCQ]"

//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun info(info: Any?) {
//        cq.logInfo(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun infoSend(info: Any?) {
//        cq.logInfoSend(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun infoRecv(info: Any?) {
//        cq.logInfoRecv(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun infoSuccess(info: Any?) {
//        cq.logInfoSuccess(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun warning(info: Any?) {
//        cq.logWarning(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun debug(info: Any?) {
//        cq.logDebug(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun fatal(info: Any?) {
//        cq.logFatal(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }
//
//    /**
//     * 输出日志
//     */
//    @JvmStatic
//    fun error(info: Any?) {
//        cq.logError(category, "[${position.name} ${Thread.currentThread().id}]: $info")
//    }

    // ******************* 国际化 ********************** //

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun info(tag: String, vararg info: Any?) {
        cq.logInfo(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun infoSend(tag: String, vararg info: Any?) {
        cq.logInfoSend(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun infoRecv(tag: String, vararg info: Any?) {
        cq.logInfoRecv(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun infoSuccess(tag: String, vararg info: Any?) {
        cq.logInfoSuccess(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun warning(tag: String, vararg info: Any?) {
        cq.logWarning(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun debug(tag: String, vararg info: Any?) {
        cq.logDebug(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun fatal(tag: String, vararg info: Any?) {
        cq.logFatal(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }

    /**
     * 国际化后输出日志
     */
    @JvmStatic
    fun error(tag: String, vararg info: Any?) {
        cq.logError(category, "[${position.name} ${Thread.currentThread().id}]: ${Language.format(tag, *info)}")
    }


}

class Test {
    fun log() {
        JCQLog.info("hello~")
    }
}
