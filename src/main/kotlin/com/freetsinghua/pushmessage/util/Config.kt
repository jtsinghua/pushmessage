package com.freetsinghua.pushmessage.util

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.server.standard.ServerEndpointExporter

/**
 * @author tsinghua
 * @date 2018/7/13
 */
@Configuration
class WebSocketConfig {

    @Bean
    fun config(): ServerEndpointExporter {
        return ServerEndpointExporter()
    }
}