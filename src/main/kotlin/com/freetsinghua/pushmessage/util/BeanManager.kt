package com.freetsinghua.pushmessage.util

import com.freetsinghua.pushmessage.dao.PushMessageDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author tsinghua
 * @date 2018/7/13
 */
@Component
class BeanManager {

    @Autowired
    fun manager(pushMessageDAO: PushMessageDAO) {
        BeanManager.pushMessageDAO = pushMessageDAO
    }

    companion object {
        lateinit var pushMessageDAO: PushMessageDAO
    }
}