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
 * This class is a basic implementation of an HTTP query.
 * @property headers an HashMap representing the headers of the query.
 * @property body the body of the request.
 * @author zmdx
 * @since 1.0
 */
abstract class HttpQuery {
    protected val headers = HashMap<String, String>()
    var body = String()

    /**
     * Return a byteArray of toString.
     * @return a byteArray.
     */
    fun toByteArray():ByteArray{
        return toString().toByteArray()
    }


    /**
     * Set a header value.
     * @param  key  the header key.
     * @param  value  the header value.
     */
    fun setHeader(key:String,value:String){
        headers[key] = value
    }

    /**
     * Get a header value.
     * @param  key  the header key.
     * @return the header value.
     */
    fun getHeader(key:String):String?{
        return headers[key]
    }
}