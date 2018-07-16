package com.freetsinghua.pushmessage.domain

import com.freetsinghua.pushmessage.common.CommonConst
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * @author tsinghua
 * @date 2018/7/13
 */
@Entity
@Table(name = "messages")
data class Message(
        @Id
        @Column(name = "ID", unique = true)
        var id: String = "",
        @Column(name = "TARGET", length = 32)
        var target: String = "",
        @Column(name = "FLAG")
        var flag: String = CommonConst.NOT_SEND,
        /**
         * json String
         */
        @Column(name = "BODY")
        var body: String = "",

        @Column(name = "CREATEDATETIME")
        var createDatetime: Date = Date(),
        @Column(name = "MODIFYDATETIME")
        var modifyDatetime: Date = Date(),
        @Column(name = "REMARK")
        var remark: String = "")