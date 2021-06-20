package com.sjvvcc.sjvc5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.remotemonster.sdk.RemonCall
import com.sjvvcc.sjvc5.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    lateinit var binding : ActivityVideoBinding
    var remonCall : RemonCall? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        remonCall = RemonCall.builder()
            .context(this)
            .serviceId("SERVICEID1")
            .key("1234567890")
            .videoCodec("VP8")
            .videoWidth(640)
            .videoHeight(480)
            .localView(binding.localView)
            .remoteView(binding.remoteView)
            .build()
        val channelID = intent.getStringExtra("channelID")
        remonCall?.connect(channelID)
        remonCall?.onClose{
            //상대방이 종료했을 경우
            finish()
        }
    }

    override fun onDestroy() {
        remonCall?.close()
        super.onDestroy()
    }
}