package com.freetsinghua.pushmessage.controller

import com.alibaba.fastjson.JSONObject
import com.freetsinghua.pushmessage.common.ErrorCodeAndMessage
import com.freetsinghua.pushmessage.service.PushMessageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author tsinghua
 * @date 2018/7/13
 */
@Controller
@RequestMapping("/pushMessage")
class PushMessageController {

    @Autowired
    private lateinit var pushMessageService: PushMessageService

    companion object {
        val LOG = LoggerFactory.getLogger(PushMessageController::class.java)
        const val STATUS = "status"
        const val MSG = "msg"
        const val SUCCESS = "success"
        const val ERROR = "error"
    }

    /**
     * {
     *  userId: 'xxxxx',
     *  msg: 'wwwwww'
     * }
     * @param msg
     */
    @PostMapping(value = ["/sendMessage"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    @ResponseBody
    fun sendMessage(@RequestBody msg: String): String {
        val map = HashMap<String, String>()

        try {
            val status = pushMessageService.sendMessage(msg)

            when (status) {
                ErrorCodeAndMessage.CONNECTION_ERROR.errorCode -> map.put(MSG, ErrorCodeAndMessage.CONNECTION_ERROR.errorMessage)
                ErrorCodeAndMessage.DATA_ACCESS_ERROR.errorCode -> map.put(MSG, ErrorCodeAndMessage.DATA_ACCESS_ERROR.errorMessage)
                ErrorCodeAndMessage.OTHER_UNKNOWN_ERROR.errorCode -> map.put(MSG, ErrorCodeAndMessage.OTHER_UNKNOWN_ERROR.errorMessage)
                ErrorCodeAndMessage.SUCCESS.errorCode -> map.put(MSG, ErrorCodeAndMessage.SUCCESS.errorMessage)
                ErrorCodeAndMessage.ILLEGAL_ARGUMENT.errorCode -> map.put(MSG, ErrorCodeAndMessage.ILLEGAL_ARGUMENT.errorMessage)
            }

            map.put(STATUS, status)
        } catch (e: Exception) {
            map.put(STATUS, ERROR)
        }

        return JSONObject.toJSONString(map)
    }
}