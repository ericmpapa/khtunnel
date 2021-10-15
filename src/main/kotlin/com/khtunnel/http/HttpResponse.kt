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
 * This class is a basic implementation of an HTTP response.
 * @property version the HTTP version.
 * @property code the response code.
 * @property message the response message.
 * @constructor creates an empty success (200) response.
 * @author zmdx
 * @since 1.0
*/

class HttpResponse(var version:String = "HTTP/1.1",
                   var code:Int=200,
                   var message:String = "OK"): HttpQuery(){
    companion object{
        const val RESPONSE_OK = 200
        const val RESPONSE_BAD_REQUEST = 400
        const val RESPONSE_UNAUTHORIZED = 401
        const val RESPONSE_FORBIDDEN = 400
        const val RESPONSE_SERVER_ERROR = 500
    }

    override fun toString():String{
        var ret = "$version $code $message\r\n"
        for((key,value) in headers){
            ret += "$key:$value\r\n"
        }
        ret += "\r\n"
        ret += body
        ret += "\r\n\r\n"
        return ret
    }
}