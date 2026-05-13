package br.com.filacidada.utils

class WebSocketManager {
    fun broadcast(room: String, event: String, data: Any?) {
        println("WebSocket Broadcast para $room: Evento $event")
    }
}