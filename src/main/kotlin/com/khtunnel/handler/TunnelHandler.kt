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

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.util.*

/**
 * This class handles the tunneling mechanism.
 * @property iins the incoming socket's InputStream.
 * @property ious the incoming socket's OutputStream.
 * @property oous the outgoing socket's OutputStream.
 * @property readBufferSize the size of the reading buffer.
 * @author ericmpapa
 * @since 1.0
 */

abstract class TunnelHandler(inSocket: Socket,outSocket: Socket):Runnable {
    protected val iins = inSocket.getInputStream()
    protected val ious = inSocket.getOutputStream()
    protected val oous = outSocket.getOutputStream()

    var readBufferSize:Int = 1024;

    /**
     * Decode a Base64 ByteArray.
     * @return a decoded ByteArray.
     */
    protected open fun decode(src:ByteArray):ByteArray{
        return decompress(Base64.getDecoder().decode(src))
    }

    /**
     * Encode a ByteArray in Base64 format.
     * @return a Base64 encoded ByteArray.
     */
    protected open fun encode(src:ByteArray):ByteArray{
        return Base64.getEncoder().encode(compress(src))
    }

    /**
     * Compress a ByteArray in Framed LZ4 format.
     * @return a Framed LZ4 compressed ByteArray.
     */
    private fun compress(src:ByteArray):ByteArray{
        val bOut = ByteArrayOutputStream()
        val zOut = FramedLZ4CompressorOutputStream(bOut)
        zOut.write(src)
        zOut.flush()
        zOut.close()
        val ret = bOut.toByteArray()
        bOut.close()
        return ret
    }

    /**
     * Decompress a Framed LZ4 compressed ByteArray.
     * @return a decompressed ByteArray.
     */
    private fun decompress(src:ByteArray):ByteArray{
        var ret = ByteArray(0)
        val buffer = ByteArray(100)
        val bIn = ByteArrayInputStream(src)
        val zIn = FramedLZ4CompressorInputStream(bIn)
        while (true){
            val byteCount = zIn.read(buffer)
            if(byteCount == -1) break
            ret += buffer.slice(0 until byteCount)
        }
        bIn.close()
        zIn.close()
        return ret
    }
}
