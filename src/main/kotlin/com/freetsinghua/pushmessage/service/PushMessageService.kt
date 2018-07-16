package com.freetsinghua.pushmessage.service

import com.alibaba.fastjson.JSONObject
import com.freetsinghua.pushmessage.common.ErrorCodeAndMessage
import com.freetsinghua.pushmessage.dao.PushMessageDAO
import com.freetsinghua.pushmessage.domain.Message
import com.freetsinghua.pushmessage.util.BeanManager
import com.freetsinghua.pushmessage.util.SendMessageFailureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import javax.websocket.*
import javax.websocket.server.ServerEndpoint

interface Base {
    companion object {
        val userSessionMap: ConcurrentHashMap<String, Session> = ConcurrentHashMap(0)
        const val USERID = "userId"
        const val MSG = "msg"
    }
}

/**
 * @author tsinghua
 * @date 2018/7/13
 */
@Component
@ServerEndpoint("/pushmsg/linkpoint")
class PushMessageHandler : Base {

    companion object {
        private val sessions: CopyOnWriteArraySet<Session> = CopyOnWriteArraySet()
        private val LOG: Logger = LoggerFactory.getLogger(PushMessageHandler::class.java)
        private const val TAG = "com.freetsinghua.pushmessage.service.PushMessageHandler"
    }

    @Autowired
    private lateinit var pushMessageDAO: PushMessageDAO

    @OnOpen
    fun onOpen(session: Session) {
        sessions.add(session)
        LOG.info(TAG + ".onOpen: new connection.")
    }

    @OnClose
    fun onClose(session: Session) {
        sessions.remove(session)
        LOG.info(TAG + ".onClose: session[${session.id}] is closed.")
        if (session.isOpen) {
            session.close()
        }
    }

    @OnError
    fun onError(session: Session, exception: Throwable) {
        sessions.remove(session)
        LOG.info(TAG + ".onError: ${exception.message}")

        if (session.isOpen) {
            session.close()
        }
    }

    /**
     * ws连接建立后，客户端传过来一个标志字段，在服务器注册，用于推送消息
     * @param session 会话
     * @param msg json字符串
     */
    @Synchronized
    @OnMessage
    fun onMessage(session: Session, msg: String) {
        val obj = JSONObject.parseObject(msg)
        val userId = obj.getString(Base.USERID)

        val ss = Base.userSessionMap.get(userId)

        if (ss == null) {
            Base.userSessionMap.put(userId, session)
        } else {
            if (ss != session) {
                sessions.remove(ss)
                ss.close()
                Base.userSessionMap.replace(userId, session)
            }
        }

        check()

        //若是数据库中有未发送的数据，则查出来，发送
        val list = pushMessageDAO.query(userId)

        list.forEach { message ->
            val msg = JSONObject.toJSONString(message.body)
            session.asyncRemote.sendText(msg) {
                if (!it.isOK) {
                    LOG.info(TAG + ".onMessage: ${it.exception.message}")
                } else {
                    pushMessageDAO.updateMessage(message.id)
                    LOG.info(TAG + ".onMessage: success!")
                }
            }
        }
    }

    private fun check() {
        pushMessageDAO = BeanManager.pushMessageDAO
    }
}

@Component
class PushMessageService : Base {

    companion object {
        private const val TAG = "com.freetsinghua.pushmessage.service.PushMessageService"
    }

    @Autowired
    private lateinit var pushMessageDAO: PushMessageDAO

    val LOG = LoggerFactory.getLogger(PushMessageService::class.java)

    fun sendMessage(msg: String): String {
        //校验所需字段是否齐全
        val validateResult = invalidate(msg)

        //若是参数不符合要求，直接返回
        if (!validateResult.equals(ErrorCodeAndMessage.SUCCESS.errorCode)) {
            return validateResult
        }

        //处理发送消息的逻辑
        val obj = JSONObject.parseObject(msg)

        check()

        val session = Base.userSessionMap.get(obj.getString(Base.USERID))

        if (session == null || !session!!.isOpen) {
            return saveMessage(msg)
        }

        try {
            session.asyncRemote.sendText(obj.getString("msg")) {
                if (!it.isOK) {
                    throw SendMessageFailureException("发送失败!ex: ${it.exception.message}")
                }
            }
            return ErrorCodeAndMessage.SUCCESS.errorCode
        } catch (e: Exception) {
            LOG.info(TAG + ".sendMessage: ${e.message}")
            return if (!saveMessage(msg).equals(ErrorCodeAndMessage.SUCCESS.errorCode)) {
                ErrorCodeAndMessage.DATA_ACCESS_ERROR.errorCode
            } else {
                ErrorCodeAndMessage.CONNECTION_ERROR.errorCode
            }
        }
    }

    private fun saveMessage(msg: String): String {
        //
        val obj = JSONObject.parseObject(msg)
        val userId = obj.getString(Base.USERID)
        val body = obj.getString(Base.MSG)

        val id = UUID.randomUUID().toString().replace("-", "")

        val message = Message(id = id, target = userId, body = body)

        pushMessageDAO.save(message)

        return ErrorCodeAndMessage.SUCCESS.errorCode
    }

    private fun invalidate(msg: String): String {
        //标志字段
        val obj = JSONObject.parseObject(msg)
        val userid = obj.getString(Base.USERID)

        if (userid == null) {
            return ErrorCodeAndMessage.ILLEGAL_ARGUMENT.errorCode
        }

        val message = obj.getString("msg")
        if (message.isNullOrEmpty()) {
            return ErrorCodeAndMessage.ILLEGAL_ARGUMENT.errorCode
        }

        return ErrorCodeAndMessage.SUCCESS.errorCode
    }

    private fun check() {
        pushMessageDAO = BeanManager.pushMessageDAO
    }
}
