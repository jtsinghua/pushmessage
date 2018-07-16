package com.freetsinghua.pushmessage.dao

import com.freetsinghua.pushmessage.common.CommonConst
import com.freetsinghua.pushmessage.domain.Message
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * @author tsinghua
 * @date 2018/7/13
 */
@Repository
@Transactional
interface PushMessageDAO : CrudRepository<Message, String> {

    @Modifying
    @Query("UPDATE Message m SET m.flag = ${CommonConst.ALREADY_SEND} WHERE m.id = :id")
    fun updateMessage(@Param("id") id: String)

    @Query("SELECT m FROM Message m WHERE m.target = :userId AND m.flag = ${CommonConst.NOT_SEND}")
    fun query(@Param("userId") userId: String): List<Message>
}