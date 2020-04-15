package com.simplerobot.component.jcq

import com.forte.qqrobot.exception.RobotRuntimeException
import com.sobte.cqp.jcq.entity.CQStatus


/**
 * JCQ 送信异常
 */
open class JCQSenderException : RobotRuntimeException {
    constructor() : super()
    constructor(message: String?, vararg format: Any?) : super(message, *format)
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?, vararg format: Any?) : super(message, cause, *format)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean, vararg format: Any?) : super(message, cause, enableSuppression, writableStackTrace, *format)
    constructor(pointless: Int, message: String?) : super(pointless, message)
    constructor(pointless: Int, message: String?, cause: Throwable?) : super(pointless, message, cause)
    constructor(pointless: Int, message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(pointless, message, cause, enableSuppression, writableStackTrace)

    /** 根据响应码提供工厂方法 */
    companion object {
        @JvmStatic
        fun failedByStatus(statusCode: Int) = if (statusCode < 0) {
            val status = CQStatus.getStatus(statusCode)

            JCQSenderException("failed", status.id, status.msg)
        } else {
            null
        }

    }

}