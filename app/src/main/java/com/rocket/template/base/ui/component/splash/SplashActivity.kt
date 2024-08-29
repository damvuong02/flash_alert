package com.rocket.template.base.ui.component.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.util.Log
import androidx.core.content.ContextCompat
import com.ads.control.ads.NoDoorAd
import com.ads.control.ads.NoDoorAdCallback
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.rocket.template.base.BuildConfig
import com.rocket.template.base.R
import com.rocket.template.base.ads.AdsConfig
import com.rocket.template.base.ads.AdsConfig.loadNativeLanguage
import com.rocket.template.base.ads.AdsConfig.loadNativeOnBoarding
import com.rocket.template.base.ads.RemoteConfigUtils
import com.rocket.template.base.app.AppConstants
import com.rocket.template.base.app.AppConstants.KEY_FIRST_ON_BOARDING
import com.rocket.template.base.app.AppConstants.KEY_SELECT_LANGUAGE
import com.rocket.template.base.databinding.ActivitySplashBinding
import com.rocket.template.base.ui.bases.BaseActivity
import com.rocket.template.base.ui.bases.ext.isNetwork
import com.rocket.template.base.utils.EasyPreferences
import com.rocket.template.base.utils.EasyPreferences.get
import com.rocket.template.base.utils.Routes
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(), RemoteConfigUtils.Listener {
    private var getConfigSuccess = false
    private var selectLanguage = false
    private var selectOnBoarding = false

    override fun getLayoutActivity() = R.layout.activity_splash

    override fun initViews() {
        super.initViews()
        MobileAds.initialize(this) {}

        selectLanguage = prefs[KEY_SELECT_LANGUAGE, false] == true
        selectOnBoarding = prefs[KEY_FIRST_ON_BOARDING, false] == true
        RemoteConfigUtils.init(this)
        loadingRemoteConfig()
    }

    private fun loadingRemoteConfig() {
        object : CountDownTimer(AppConstants.DEFAULT_TIME_SPLASH, 100) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("TuanPA38", " loadingRemoteConfig onTick == $millisUntilFinished")
                if (getConfigSuccess && millisUntilFinished < AppConstants.DEFAULT_LIMIT_TIME_SPLASH) {
                    checkRemoteConfigResult()
                    cancel()
                }
            }

            override fun onFinish() {
                if (!getConfigSuccess) {
                    checkRemoteConfigResult()
                }
            }
        }.start()
    }


    private fun checkRemoteConfigResult() {
        // check if not select language => show language screen
        if (!selectLanguage) {
            loadNativeLanguage(this@SplashActivity)
        }
        // check if not select onBoarding => show onBoarding screen
        if (!selectOnBoarding) {
            loadNativeOnBoarding(this)
        }
        // check have permission or not for redirect activity
        if (ContextCompat.checkSelfPermission(
                this@SplashActivity, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            AdsConfig.loadNativePermission(this)
        }
        // if have native home need load
//        AdsConfig.loadNativeHome(this)

        if (RemoteConfigUtils.getOnInterSplash() && isNetwork(
                this@SplashActivity
            )
        ) {
            NoDoorAd.getInstance().loadSplashInterstitialAds(
                this,
                BuildConfig.admob_inter_splash,
                25000,
                5000,
                object : NoDoorAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        moveActivity()
                    }
                })
        } else {
            moveActivity()
        }
    }

    private fun moveActivity() {
        if (!selectLanguage) {
            Routes.startLanguageActivity(this, null)
            finish()
        } else

            if (ContextCompat.checkSelfPermission(
                    this@SplashActivity, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startMainActivity()
            } else {
                checkPermission()
            }

    }

    override fun onResume() {
        super.onResume()
        NoDoorAd.getInstance()
            .onCheckShowSplashWhenFail(this@SplashActivity, object : NoDoorAdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    moveActivity()
                }
            }, 1000)
    }


    private fun checkPermission() {

        if (EasyPreferences.defaultPrefs(this)
                .getBoolean(AppConstants.KEY_FIRST_ON_BOARDING, true)
        ) {
            startActivityOnBoarding()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startMainActivity()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                    startActivityPermission()

                }

                else -> {
                    // You can directly ask for the permission.
                    startActivityPermission()

                }
            }

        }

    }


    private fun startActivityOnBoarding() {
        Routes.startOnBoardingActivity(this)

    }

    private fun startActivityPermission() {
        Routes.startPermissionActivity(this)

    }

    private fun startMainActivity() {
        Routes.startMainActivity(this)
    }

    override fun loadSuccess() {
        getConfigSuccess = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}