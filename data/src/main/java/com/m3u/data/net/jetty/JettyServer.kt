package com.m3u.data.net.jetty

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

class JettyServer(private val port: Int) {
    private val context: ZContext by lazy { ZContext() }
    private var socket: ZMQ.Socket? = null
    private val flow = MutableSharedFlow<String>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun start() {
        socket?.unbind("tcp://*:$port")
        socket = context.createSocket(SocketType.REP).apply {
            bind("tcp://*:$port")
            while (!Thread.currentThread().isInterrupted) {
                val reply = recv(0)
                coroutineScope.launch {
                    flow.emit(String(reply, ZMQ.CHARSET))
                }
            }
        }
    }

    fun observe(): SharedFlow<String> = flow.asSharedFlow()

    fun send(body: String) {
        socket?.send(body)
    }

    fun send(bytes: ByteArray) {
        socket?.send(bytes)
    }
}