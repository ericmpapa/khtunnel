/*
 * Copyright 2021  Eric Mpapa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.khtunnel.http

/**
 * This object implements an HTTP Parser.
 * @author ericmpapa
 * @since 1.0
 */
object HttpParser {

    /**
     * Parse an HttpQuery from a ByteArray.
     * @param byteArray the source ByteArray
     * @return an HttpRequest if the query is an HTTP request, an HttpResponse if the query is an HTTP response, null if the parsing fails.
     */
    @JvmStatic
    fun parse(byteArray:ByteArray):HttpQuery?{
        var httpQuery:HttpQuery? = null
        var query = String(byteArray)
        if(isValidRequest(query)){
            query = query.replace("\r\n\r\n","###")
            val queryParts = query.split("###")
            query = queryParts[0]
            val lines = query.split("\r\n")
            val requestLine = lines[0]
            val requestLineValues = requestLine.split(" ")
            httpQuery = if(requestLineValues.size >2){
                val method = requestLineValues[0]
                val url = requestLineValues[1]
                val version = requestLineValues[2]
                 HttpRequest(version,url,method)
            } else null
            httpQuery?.also{
                for(i in 1 until lines.size){
                    val header = lines[i].split(":")
                    if(header.size == 2){
                        val key = header[0]
                        val value = header[1]
                        it.setHeader(key,value)
                    }
                }
                if(queryParts.size > 1)it.body = queryParts[1]
            }
        }  else if(isValidResponse(query)){
            query = query.replace("\r\n\r\n","###")
            query = query.split("###")[0]
            val lines = query.split("\r\n")
            val statusLine = lines[0]
            val statusLineValues = statusLine.split(" ")
            val version = statusLineValues[0]
            val code = statusLineValues[1].toInt()
            val message = statusLineValues[2]
            httpQuery = HttpResponse(version,code,message)
            for(i in 1 until lines.size){
                val header = lines[i].split(":")
                if(header.size == 2){
                    val key = header[0]
                    val value = header[1]
                    httpQuery.setHeader(key,value)
                }
            }
        }
        return httpQuery
    }

    /**
     * Checks if query is a valid HTTP response query string.
     * @param query the query string
     * @return true if query is a valid HTTP response, false otherwise.
     */
    @JvmStatic
    fun isValidResponse(query:String):Boolean{
        return  query.startsWith("HTTP/1.1")
    }

    /**
     * Checks if query is a valid HTTP request query string.
     * @param query the query string
     * @return true if query is a valid HTTP response, false otherwise.
     */
    @JvmStatic
    fun isValidRequest(query:String):Boolean{
        return query.startsWith(HttpRequest.METHOD_CONNECT) ||
               query.startsWith(HttpRequest.METHOD_GET) ||
               query.startsWith(HttpRequest.METHOD_POST)
    }
}
