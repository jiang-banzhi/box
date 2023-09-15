package jy.tool.box.nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jy.tool.box.databinding.ActivityNfcactivityBinding
import okhttp3.internal.and
import java.nio.charset.Charset


class NFCActivity : AppCompatActivity() {
    var binding: ActivityNfcactivityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcactivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val nfcUtils = NfcUtils(this)
    }

    override fun onResume() {
        super.onResume()
        //开启前台调度系统
        NfcUtils.mNfcAdapter.enableForegroundDispatch(
            this,
            NfcUtils.mPendingIntent,
            NfcUtils.mIntentFilter,
            NfcUtils.mTechList
        )
    }

    override fun onPause() {
        super.onPause()
        //关闭前台调度系统
        NfcUtils.mNfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取NFC数据
//        val readNFCFromTag = NfcUtils.readNFCFromTag(intent)
//        val readNFCFromTag = NfcUtils.getNdefMsg(intent)
//        val readNFCFromTag = NfcUtils.readNFCId(intent)
//        Log.e("nfc===>", readNFCFromTag.toString())
//        binding!!.textview.setText(binding!!.textview.text.toString() + "\n" + readNFCFromTag)
//        NfcUtil.processIntent(intent, binding!!.textview)
        NfcIsoUtil.praseIntent(intent, binding!!.textview)
//        nfca(intent!!)
    }

    private fun nfca(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) // 获取Tag标签，既可以处理相关信息

        val nfca = NfcA.get(tag)
        try {
            nfca.connect()
            if (nfca.isConnected) { //NTAG216的芯片
                val SELECT = byteArrayOf(
                    0x30.toByte(), (
                            5.toByte() and 0x0ff).toByte()
                )
                val response = nfca.transceive(SELECT)
                nfca.close()
                if (response != null) {
                    binding!!.textview.text = String(response, Charset.forName("utf-8"))
                }
            }
        } catch (e: Exception) {
        }


    }

}