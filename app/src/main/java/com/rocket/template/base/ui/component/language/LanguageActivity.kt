package com.rocket.template.base.ui.component.language

import android.content.Intent
import android.content.res.Configuration
import com.ads.control.ads.NoDoorAd
import com.facebook.shimmer.ShimmerFrameLayout
import com.rocket.template.base.R
import com.rocket.template.base.ads.AdsConfig
import com.rocket.template.base.ads.AdsConfig.nativeAdLanguage
import com.rocket.template.base.ads.PreLoadNativeListener
import com.rocket.template.base.app.AppConstants
import com.rocket.template.base.app.GlobalApp
import com.rocket.template.base.databinding.ActivityLanguageBinding
import com.rocket.template.base.ui.bases.BaseActivity
import com.rocket.template.base.ui.bases.ext.goneView
import com.rocket.template.base.ui.bases.ext.showToastById
import com.rocket.template.base.ui.bases.ext.visibleView
import com.rocket.template.base.utils.EasyPreferences.get
import com.rocket.template.base.utils.EasyPreferences.set
import com.rocket.template.base.ui.component.main.MainActivity
import com.rocket.template.base.ui.component.onboarding.OnBoardingActivity
import com.rocket.template.base.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>(), PreLoadNativeListener {
    private var adapter: LanguageAdapter? = null
    private var model: LanguageModel? = null

    private var populateNativeAdView = false
    private var shimmerSmall: ShimmerFrameLayout? = null
    private var firstTutorial = false

    override fun getLayoutActivity() = R.layout.activity_language

    override fun initViews() {
        super.initViews()
        firstTutorial = prefs[AppConstants.KEY_FIRST_ON_BOARDING, false] == true

        shimmerSmall = findViewById(R.id.shimmer_native_large)

        adapter = LanguageAdapter(this, onClickItemLanguage = {
            adapter?.setSelectLanguage(it)
            model = it
        })
        mBinding.rclLanguage.adapter = adapter

        setLanguageDefault()

        AdsConfig.setPreLoadNativeCallback(this)
        showNativeLanguage()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.ivDone.setOnClickListener {
            if (model?.languageName != "") {

                prefs[AppConstants.KEY_SELECT_LANGUAGE] = true
                prefs[AppConstants.KEY_LANGUAGE] = model?.isoLanguage
                setLocale()

                if (!firstTutorial) {
                    Routes.startOnBoardingActivity(this)

                } else
                    gotoActivity()

                finishAffinity()
            } else {
                showToastById(R.string.please_select_language)
            }
        }
    }

    private fun gotoActivity() {
        Routes.startMainActivity(this)
    }

    private fun setLocale() {
        val language = prefs.getString(AppConstants.KEY_LANGUAGE, "en")

        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            if (language.equals("", ignoreCase = true)) return
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }

    }

    private fun setLanguageDefault() {
        val listLanguages: ArrayList<LanguageModel> = ArrayList()

        val key: String? = prefs[AppConstants.KEY_LANGUAGE, "en"]

        listLanguages.add(LanguageModel("English", "en", false, R.drawable.ic_english))
        listLanguages.add(LanguageModel("Hindi", "hi", false, R.drawable.ic_hindi))
        listLanguages.add(LanguageModel("Spanish", "es", false, R.drawable.ic_spanish))
        listLanguages.add(LanguageModel("French", "fr", false, R.drawable.ic_france))
        listLanguages.add(LanguageModel("Portuguese", "pt", false, R.drawable.ic_portugal))
        listLanguages.add(LanguageModel("Korean", "ko", false, R.drawable.ic_korean))

        if (GlobalApp.instance.getLanguage() != null && !listLanguages.contains(GlobalApp.instance.getLanguage())) {
            listLanguages.remove(listLanguages[listLanguages.size - 1])
            val modelLanguage = GlobalApp.instance.getLanguage()
            if (modelLanguage != null) {
                listLanguages.add(0, modelLanguage)
            }

        }

        for (i in listLanguages.indices) {
            if (key == listLanguages[i].isoLanguage) {
                val data = listLanguages[i]
                data.isCheck = true
                listLanguages.remove(listLanguages[i])
                listLanguages.add(0, data)
                model = data
                break
            }
        }

        adapter?.submitData(listLanguages)
    }

    private fun showNativeLanguage() {
        if (nativeAdLanguage != null) {
            Timber.e("initAdmob: $nativeAdLanguage")
            populateNativeAdView = true
            NoDoorAd.getInstance().populateNativeAdView(
                this,
                nativeAdLanguage,
                mBinding.frAds,
                shimmerSmall
            )
        } else {
            mBinding.frAds.goneView()
            Timber.d(
                "LanguageActivity initAds nativeAdViewLanguage = $nativeAdLanguage - nativeAdLanguage = $nativeAdLanguage"
            )
        }
    }

    override fun onLoadNativeSuccess() {
        showNativeLanguage()
    }

    override fun onLoadNativeFail() {
        if (nativeAdLanguage!=null) {
            mBinding.frAds.visibleView()
        }else{
            mBinding.frAds.goneView()
        }
    }
}