# pushmessage

## 推送消息

- 使用的是 Websocket技术
- 客户端使用指南

      fun main(args: Array<String>){
    
        val uri = URI("ws://localhost:8080/pushmsg/linkpoint")
        val webSocket = WebSocket(uri)
    
        webSocket.eventHandler = object : WebSocketEventHandler {
            override fun onOpen() {
                println("建立连接")
                webSocket.send("{userId: '40009'}")
            }
    
            override fun onMessage(message: WebSocketMessage?) {
                println("收到消息：" + message!!.text)
            }
    
            override fun onError(exception: IOException?) {
                println("发生错误：" + exception!!.message)
            }
    
            override fun onClose() {
                println("关闭连接")
            }
    
            override fun onPong() {
                println("pong...")
            }
    
            override fun onPing() {
                println("ping...")
            }
    
        }
    
        webSocket.connect()
    
      }
      
- 推送消息使用springmvc

  http://localhost:8080/pushMessage/sendMessage
  
  json：
    {
    	userId: '40009',
    	msg: '这就是测试啊！！'
    }
