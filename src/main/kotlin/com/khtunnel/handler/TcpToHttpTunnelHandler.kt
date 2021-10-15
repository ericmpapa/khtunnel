/*
 * Copyright 2021  Eric Mpapa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.khtunnel.handler

import com.khtunnel.http.HttpRequest
import java.net.Socket
import java.net.SocketException
import java.nio.charset.StandardCharsets

/**
 * This class handles the TCP to HTTP tunneling mechanism.
 * @author ericmpapa
 * @since 1.0
 */

open class TcpToHttpTunnelHandler(
    inSocket:Socket,
    outSocket:Socket
): TunnelHandler(inSocket,outSocket) {
    /**
     * Add extra headers to the HTTP request that will be sent on the network.
     * @param httpRequest The HTTP request that will be sent on the network.
     */
    open fun addHeaders(httpRequest:HttpRequest){}

    override fun run(){
        try{
            val readBuffer = ByteArray(readBufferSize)
            while(true){
                val byteCount = iins.read(readBuffer)
                if(byteCount == -1 ) break
                val data = readBuffer.sliceArray(0 until byteCount)
                val httpRequest = HttpRequest()
                val body = String(encode(data),StandardCharsets.UTF_8)
                httpRequest.setHeader("Content-Length", body.length.toString())
                addHeaders(httpRequest)
                httpRequest.body = body
                oous.write(httpRequest.toByteArray())
                oous.flush()
            }
        }
        catch (e: SocketException){}
        catch (e: Exception){
            e.printStackTrace()
        } finally{
            try{
                iins.close()
                oous.close()
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}
