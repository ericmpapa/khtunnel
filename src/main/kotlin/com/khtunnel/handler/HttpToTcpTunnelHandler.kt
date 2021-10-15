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

import com.khtunnel.http.HttpParser
import com.khtunnel.http.HttpRequest
import com.khtunnel.http.HttpResponse

import java.net.Socket
import java.net.SocketException


/**
 * This class handles the HTTP to TCP tunneling mechanism.
 * @property buffer the buffer holding data to be transmitted.
 * @author zmdx
 * @since 1.0
 */

open class HttpToTcpTunnelHandler(
    inSocket:Socket,
    outSocket:Socket
    ):TunnelHandler(inSocket,outSocket) {

    private var buffer = ByteArray(0)

    /**
     * Send the buffered data
     */
    private fun sendBufferedData(){
        oous.write(buffer)
        oous.flush()
    }

    /**
     * Buffer incoming data.
     * @param httpRequest the httpRequest to handle.
     */
    protected open fun bufferData(httpRequest: HttpRequest){
        val payload = httpRequest.body.toByteArray()
        buffer = decode(payload)
    }

    /**
     * Processes the incoming Http Query.
     * @param httpResponse the httResponse to handle.
     */
    protected open fun handleHttpResponse(httpResponse: HttpResponse){}


    override fun run(){
        try{
            var dataBuffer = ByteArray(0)
            val readBuffer = ByteArray(readBufferSize)
            var concatenated = false
            while(true){
                val data = if(!concatenated){
                    val byteCount = iins.read(readBuffer)
                    if(byteCount == -1 && dataBuffer.isEmpty()) break
                    dataBuffer + readBuffer.sliceArray(0 until byteCount)
                } else dataBuffer
                val httpQuery = HttpParser.parse(data)
                var contentLength = 1
                concatenated = false
                httpQuery?.also{
                    if(httpQuery is HttpRequest){
                        contentLength = httpQuery.getHeader("Content-Length")?.toInt() ?: 0
                        if(httpQuery.body.length == contentLength && contentLength > 0){
                            bufferData(httpQuery)
                        } else {
                            contentLength = 0
                        }
                    }
                    else handleHttpResponse(httpQuery as HttpResponse)
                    val dataSize = data.size
                    val querySize = it.toByteArray().size
                    val remainingDataSize = dataSize - querySize
                    dataBuffer = if(remainingDataSize > 0){
                        /* 2 Request has been concatenated due to TCP streaming nature.*/
                        sendBufferedData()
                        concatenated = true
                        data.sliceArray(querySize until dataSize)
                    }
                    else if(contentLength == 0) {
                        /* The request is truncated.*/
                        buffer = ByteArray(0)
                        data
                    } else {
                        /* No concatenation or truncation.*/
                        sendBufferedData()
                        ByteArray(0)
                    }
                }
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
