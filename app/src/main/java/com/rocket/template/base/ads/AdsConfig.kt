package com.rocket.template.base.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.ads.control.ads.NoDoorAd
import com.ads.control.ads.NoDoorAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.rocket.template.base.BuildConfig
import com.rocket.template.base.R
import com.rocket.template.base.ui.bases.ext.isNetwork

object AdsConfig {

    @SuppressLint("StaticFieldLeak")
    var nativeAdLanguage: ApNativeAd? = null
    @SuppressLint("StaticFieldLeak")
    var nativeAdOnBoarding: ApNativeAd? = null
    @SuppressLint("StaticFieldLeak")
    var nativeAdPermission: ApNativeAd? = null

    var preLoadNativeListener: PreLoadNativeListener? = null


    fun setPreLoadNativeCallback(listener: PreLoadNativeListener) {
        preLoadNativeListener = listener
    }

    fun loadNativeLanguage(activity: Activity) {
        if (nativeAdLanguage == null) {
            if (RemoteConfigUtils.getOnNativeLanguage()) {
                NoDoorAd.getInstance().loadNativeAdResultCallback(activity,
                    BuildConfig.admob_native_language,
                    R.layout.layout_native_language,
                    object : NoDoorAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdLanguage = nativeAd
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeSuccess()
                            }
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeFail()
                            }
                        }
                    })
            }
        }
    }

    fun loadNativeOnBoarding(activity: Activity) {
        if (nativeAdOnBoarding == null) {
            if (RemoteConfigUtils.getOnNativeOnBoarding() && activity.isNetwork()) {
                NoDoorAd.getInstance().loadNativeAdResultCallback(activity,
                    BuildConfig.admob_native_on_boarding,
                    R.layout.layout_native_on_boarding,
                    object : NoDoorAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdOnBoarding = nativeAd
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeSuccess()
                            }
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeFail()
                            }
                        }
                    })
            }
        }
    }

    fun loadNativePermission(activity: Activity) {
        if (nativeAdPermission == null) {
            if (RemoteConfigUtils.getOnNativePermission() && activity.isNetwork()) {
                NoDoorAd.getInstance().loadNativeAdResultCallback(activity,
                    BuildConfig.admob_native_permission,
                    R.layout.layout_native_permission,
                    object : NoDoorAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdPermission = nativeAd
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeSuccess()
                            }
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeFail()
                            }
                        }
                    })
            }
        }
    }

    fun loadBanner(activity: AppCompatActivity, id: String, frAds: FrameLayout, bool: Boolean) {
        if (isNetwork(activity) && bool) {
            NoDoorAd.getInstance().loadBanner(activity, id)
        } else {
            frAds.removeAllViews()
        }
    }

}
