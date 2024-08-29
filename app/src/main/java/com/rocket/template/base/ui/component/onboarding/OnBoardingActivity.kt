package com.rocket.template.base.ui.component.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ads.control.ads.NoDoorAd
import com.rocket.template.base.R
import com.rocket.template.base.ads.AdsConfig
import com.rocket.template.base.ads.PreLoadNativeListener
import com.rocket.template.base.app.AppConstants
import com.rocket.template.base.databinding.ActivityOnboardingBinding
import com.rocket.template.base.models.GuideModel
import com.rocket.template.base.ui.bases.BaseActivity
import com.rocket.template.base.ui.bases.ext.goneView
import com.rocket.template.base.ui.bases.ext.isNetwork
import com.rocket.template.base.ui.bases.ext.visibleView
import com.rocket.template.base.ui.component.onboarding.adapter.OnBoardingAdapter
import com.rocket.template.base.ui.component.permision.PermissionActivity
import com.rocket.template.base.utils.EasyPreferences
import com.rocket.template.base.utils.EasyPreferences.set
import com.rocket.template.base.utils.Routes
import kotlin.math.abs

class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>(), PreLoadNativeListener {
    private var tutorialAdapter: OnBoardingAdapter? = null
    private var posViewPager = 0
    private var populateNative = false

    override fun getLayoutActivity(): Int = R.layout.activity_onboarding

    override fun initViews() {

        mBinding.tvGetStart.text = getString(R.string.next)
        tutorialAdapter = OnBoardingAdapter()
        mBinding.viewPager2.adapter = tutorialAdapter
        mBinding.viewPager2.clipToPadding = false
        mBinding.viewPager2.clipChildren = false
        mBinding.viewPager2.offscreenPageLimit = 3
        mBinding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(100))
        compositePageTransformer.addTransformer { view, position ->
            val r = 1 - abs(position)
            view.scaleY = 0.8f + r * 0.2f
            val absPosition = abs(position)
            view.alpha = 1.0f - (1.0f - 0.3f) * absPosition
        }

        mBinding.viewPager2.setPageTransformer(compositePageTransformer)
        mBinding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("InvalidAnalyticsName", "UseCompatLoadingForDrawables")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        posViewPager = 0
                        mBinding.tvGetStart.text = getString(R.string.next)
                        mBinding.view1.setImageResource(R.drawable.ic_view_select)
                        mBinding.view2.setImageResource(R.drawable.ic_view_un_select)
                        mBinding.view3.setImageResource(R.drawable.ic_view_un_select)
                    }
                    1 -> {
                        posViewPager = 1
                        mBinding.tvGetStart.text = getString(R.string.next)
                        mBinding.view1.setImageResource(R.drawable.ic_view_un_select)
                        mBinding.view2.setImageResource(R.drawable.ic_view_select)
                        mBinding.view3.setImageResource(R.drawable.ic_view_un_select)
                    }
                    2 -> {
                        posViewPager = 2
                        mBinding.tvGetStart.text = getString(R.string.get_started)
                        mBinding.view1.setImageResource(R.drawable.ic_view_un_select)
                        mBinding.view2.setImageResource(R.drawable.ic_view_un_select)
                        mBinding.view3.setImageResource(R.drawable.ic_view_select)
                    }
                }
            }
        })

        getData()
        AdsConfig.setPreLoadNativeCallback(this)
        showNativeOnBoardingAds()

    }

    override fun onClickViews() {
        mBinding.tvGetStart.setOnClickListener {
            when (posViewPager) {
                0 -> {
                    mBinding.viewPager2.currentItem = posViewPager + 1
                    mBinding.view1.setImageResource(R.drawable.ic_view_un_select)
                    mBinding.view2.setImageResource(R.drawable.ic_view_select)
                    mBinding.view3.setImageResource(R.drawable.ic_view_un_select)
                }
                1 -> {
                    mBinding.viewPager2.currentItem = posViewPager + 1
                    mBinding.tvGetStart.text = getString(R.string.get_started)
                    mBinding.view1.setImageResource(R.drawable.ic_view_un_select)
                    mBinding.view2.setImageResource(R.drawable.ic_view_un_select)
                    mBinding.view3.setImageResource(R.drawable.ic_view_select)
                }
                2 -> {
                    gotoNextScreen()
                }
            }
        }
    }

    private fun showNativeOnBoardingAds() {
        if (!isNetwork(this)) {
            mBinding.frAds.goneView()
            return
        }

        if (!populateNative) {
            mBinding.frAds.visibleView()
            if (AdsConfig.nativeAdOnBoarding != null) {
                NoDoorAd.getInstance().populateNativeAdView(
                    this,
                    AdsConfig.nativeAdOnBoarding,
                    mBinding.frAds,
                    mBinding.shimmerAds.shimmerSmall
                )
                populateNative = true
            } else {
                mBinding.frAds.goneView()
            }
        }
    }

    private fun gotoNextScreen() {

        EasyPreferences.defaultPrefs(this)[AppConstants.KEY_FIRST_ON_BOARDING] = false
        Routes.startPermissionActivity(this)
        finish()

    }

    private fun getData() {
        val mHelpGuide: ArrayList<GuideModel> = ArrayList()
        mHelpGuide.add(
            GuideModel(
                R.drawable.ic_vietnamese,
                R.string.app_name,
                R.string.app_name,

                )
        )
        mHelpGuide.add(
            GuideModel(
                R.drawable.ic_vietnamese,
                R.string.app_name,
                R.string.app_name,
            )
        )
        mHelpGuide.add(
            GuideModel(
                R.drawable.ic_vietnamese,
                R.string.app_name,
                R.string.app_name,
            )
        )
        tutorialAdapter?.submitData(mHelpGuide as List<GuideModel>)
    }

    override fun onLoadNativeSuccess() {
        showNativeOnBoardingAds()
    }

    override fun onLoadNativeFail() {
        if (AdsConfig.nativeAdOnBoarding !=null) {
            mBinding.frAds.visibleView()
        }else{
            mBinding.frAds.goneView()
        }
    }

}