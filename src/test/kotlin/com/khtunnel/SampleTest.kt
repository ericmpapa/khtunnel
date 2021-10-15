/*
 * Copyright 2021  Eric Mpapa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.khtunnel

import com.khtunnel.handler.HttpToTcpTunnelHandler
import com.khtunnel.handler.TcpToHttpTunnelHandler
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


/*
    SIMPLE TEST CASE
    ----------------
    TCP -> HTTP -> HTTP -> TCP
 */

class SampleTest {
    private lateinit var client:Socket
    private lateinit var server1:ServerSocket
    private lateinit var server2:ServerSocket
    private lateinit var server3:ServerSocket

    private fun generateRandomString(bytesCount:Int):String{
        var ret = ""
        for(i in 1..bytesCount){
            ret += Char(33 + Random.nextInt(125-33))
        }
        return ret
    }

    private val expectedValue = generateRandomString(800)

    @BeforeTest
    fun init(){
        server1 = ServerSocket(10111)
        server2 = ServerSocket(10112)
        server3 = ServerSocket(10113)

    }

    @Test
    fun test(){
        val latch = CountDownLatch(1)
        val actualValue = StringBuilder()
        Thread{
            val socket = server3.accept()
            val ins = socket.getInputStream()
            val buffer = ByteArray(800)
            var data = ByteArray(0)
            while(true){
                val bytesCount = ins.read(buffer)
                if(bytesCount == -1 ) break
                data += buffer
            }
            actualValue.append(String(data))
            latch.countDown()
        }.start()

        Thread{
            val outSocket = Socket("127.0.0.1",10113)
            val inSocket = server2.accept()
            Thread(HttpToTcpTunnelHandler(inSocket,outSocket)).start()
        }.start()

        Thread{
            val outSocket = Socket("127.0.0.1",10112)
            val inSocket = server1.accept() // receiving from client
            Thread(TcpToHttpTunnelHandler(inSocket,outSocket)).start()
        }.start()

        client = Socket("127.0.0.1",10111)
        val ous = client.getOutputStream()
        ous.write(expectedValue.toByteArray())
        ous.flush()
        Thread.sleep(2000)
        client.close()
        latch.await()
        assertEquals(expectedValue,actualValue.toString())
    }
}