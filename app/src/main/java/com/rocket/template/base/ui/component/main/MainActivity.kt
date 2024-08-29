package com.rocket.template.base.ui.component.main

import androidx.activity.OnBackPressedCallback
import com.rocket.template.base.R
import com.rocket.template.base.app.AppConstants
import com.rocket.template.base.databinding.ActivityMainBinding
import com.rocket.template.base.ui.bases.BaseActivity
import com.rocket.template.base.ui.bases.ext.click
import com.rocket.template.base.utils.ConnectionLiveData
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getLayoutActivity(): Int = R.layout.activity_main

    override fun onResume() {
        super.onResume()
        ConnectionLiveData(this).observe(this) { isNetwork ->
            if (isNetwork) {
                Timber.d("network on")
            } else {
                Timber.d("network off")
            }
        }
    }

    override fun initViews() {
        super.initViews()
        initOnBackPressed()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.buttonHello.click {
//            NoDoorTrackingHelper.logEventClick(this::class.java.simpleName, "buttonHello", null)
        }
    }


    private fun initOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (prefs.getBoolean(AppConstants.KEY_SET_SHOW_DIALOG_RATE, true)) {
                    showDialogRate(true)
                } else {
                    finish()
                }
            }
        })
    }
}
