package com.freetsinghua.pushmessage.common

/**
 * @author tsinghua
 * @date 2018/7/13
 */
enum class ErrorCodeAndMessage private constructor(val errorCode: String, val errorMessage: String) {
    /**
     * 错误
     */
    SUCCESS("00000000", "操作成功"),
    DATA_ACCESS_ERROR("01111111", "数据库访问错误"),
    CONNECTION_ERROR("02222222", "网络连接错误"),
    OTHER_UNKNOWN_ERROR("03333333", "其他未知错误"),
    ILLEGAL_ARGUMENT("04444444", "参数错误")
}
