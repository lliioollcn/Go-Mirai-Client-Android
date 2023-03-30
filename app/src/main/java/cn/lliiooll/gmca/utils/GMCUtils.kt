package cn.lliiooll.gmca.utils

class GMCUtils {
    companion object {
        private val GMC_URL = "http://localhost:9000/";
        fun isEnable(): Boolean {
            return JavaUtils.isConnected(GMC_URL)
        }
    }
}