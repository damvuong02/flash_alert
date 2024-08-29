package com.rocket.template.base.ui.component.permision

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.ads.control.ads.NoDoorAd
import com.rocket.template.base.R
import com.rocket.template.base.ads.AdsConfig
import com.rocket.template.base.ads.PreLoadNativeListener
import com.rocket.template.base.databinding.ActivityPermissionBinding
import com.rocket.template.base.ui.bases.BaseActivity
import com.rocket.template.base.ui.bases.ext.goneView
import com.rocket.template.base.ui.bases.ext.visibleView
import com.rocket.template.base.utils.Routes
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.EasyPermissions.hasPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import timber.log.Timber


class PermissionActivity : BaseActivity<ActivityPermissionBinding>(), PreLoadNativeListener {

    private var populateNativeAdView = false

    override fun getLayoutActivity(): Int = R.layout.activity_permission

    override fun initViews() {
        super.initViews()

        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                Routes.startMainActivity(this)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.

            }

            else -> {
                // You can directly ask for the permission.
            }
        }

        AdsConfig.setPreLoadNativeCallback(this)
        showNativePermission()

    }

    private fun showNativePermission() {
        if (AdsConfig.nativeAdPermission != null) {
            Timber.e("initAdmob: ${AdsConfig.nativeAdPermission}")
            populateNativeAdView = true
            NoDoorAd.getInstance().populateNativeAdView(
                this,
                AdsConfig.nativeAdPermission,
                mBinding.frAds,
                mBinding.layoutShimmer.shimmerNativePer
            )
        } else {
            mBinding.frAds.removeAllViews()
            Timber.d(
                "LanguageActivity initAds nativeAdViewLanguage = ${AdsConfig.nativeAdLanguage} - nativeAdLanguage = ${AdsConfig.nativeAdLanguage}"
            )
        }
    }


    override fun onClickViews() {
        super.onClickViews()
    }

    fun checkPermission(view: View) {
        requestPermission()
    }


    companion object {
        const val RECORD_PERMISSION = 6868
    }


    @AfterPermissionGranted(RECORD_PERMISSION)
    private fun requestPermission() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        }
        if (hasPermissions(this, *perms)) {
            Routes.startMainActivity(this)
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.txt_request_record_permission), RECORD_PERMISSION, *perms
            )

        }
    }


    override fun onResume() {
        super.onResume()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onLoadNativeSuccess() {
        showNativePermission()
    }

    override fun onLoadNativeFail() {
        if (AdsConfig.nativeAdPermission !=null) {
            mBinding.frAds.visibleView()
        }else{
            mBinding.frAds.goneView()
        }
    }


}