package com.xiaoxiaoying.spider.http

import com.orhanobut.logger.Logger
import java.net.InetAddress
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
class SSLSocketFactoryCompat : SSLSocketFactory() {
    private var protocols = emptyArray<String>()
    private val socketFactory = getSSLSocketFactory()

    init {
        try {
            val socket = getDefault().createSocket()
            if (socket is SSLSocket) {
                protocols =
                    socket.supportedProtocols?.filter { it.contains("SSL", ignoreCase = true) }
                        ?.toTypedArray() ?: emptyArray()


            }

        } catch (e: Exception) {
            Interceptor.logT()
            Logger.e(e, "e")
        }
    }

    private fun getSSLSocketFactory(): SSLSocketFactory {
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, arrayOf(xManager), SecureRandom())
        return sc.socketFactory
    }

    private fun SSLSocket.upgradeTLS() {
        enabledProtocols = protocols
    }

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        val ssl = socketFactory.createSocket(s, host, port, autoClose)
        if (ssl is SSLSocket) {
            ssl.upgradeTLS()
        }
        return ssl
    }

    override fun createSocket(host: String?, port: Int): Socket {
        val ssl = socketFactory.createSocket(host, port)
        if (ssl is SSLSocket) {
            ssl.upgradeTLS()
        }
        return ssl
    }

    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket {
        val ssl = socketFactory.createSocket(host, port,localHost, localPort)
        if (ssl is SSLSocket) {
            ssl.upgradeTLS()
        }
        return ssl
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        val ssl = socketFactory.createSocket(host, port)
        if (ssl is SSLSocket) {
            ssl.upgradeTLS()
        }
        return ssl
    }

    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket {
        val ssl = socketFactory.createSocket(address, port,localAddress, localPort)
        if (ssl is SSLSocket) {
            ssl.upgradeTLS()
        }
        return ssl
    }

    override fun getDefaultCipherSuites(): Array<String> = emptyArray()

    override fun getSupportedCipherSuites(): Array<String> = emptyArray()
    companion object {

        @JvmStatic
        val xManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOf()
            }

        }

        fun getHostnameVerifier(): HostnameVerifier = HostnameVerifier { _, _ ->
            true
        }
    }
}