package com.rocket.template.base.ui.bases

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.rocket.template.base.app.AppConstants
import com.rocket.template.base.ui.component.dialog.RateAppDialog
import com.rocket.template.base.utils.EasyPreferences
import com.rocket.template.base.utils.EasyPreferences.set

import com.rocket.template.base.utils.Routes
import java.util.Locale

private const val TAG = "BaseActivity"

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    lateinit var mBinding: VB
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = EasyPreferences.defaultPrefs(this)

        setLocal()

        requestWindow()
        val layoutView = getLayoutActivity()
        mBinding = DataBindingUtil.setContentView(this, layoutView)
        if (intent.getStringExtra(AppConstants.KEY_TRACKING_SCREEN_FROM) != null) {
            Routes.addTrackingMoveScreen(
                intent.getStringExtra(AppConstants.KEY_TRACKING_SCREEN_FROM).toString(),
                this::class.java.simpleName
            )
        }
        mBinding.lifecycleOwner = this

        initViews()
        onResizeViews()
        onClickViews()
        observerData()
    }

    open fun setUpViews() {}

    abstract fun getLayoutActivity(): Int

    open fun requestWindow() {}

    open fun initViews() {}

    open fun onResizeViews() {}

    open fun onClickViews() {}

    open fun observerData() {}

    private fun setLocal() {
        val language: String? = prefs.getString(AppConstants.KEY_LANGUAGE, "")
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


    override fun onResume() {
        super.onResume()
        hideNavigationBar()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideNavigationBar()

    }

    private fun hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            hideSystemUIBeloR()
        }

    }

    private fun hideSystemUIBeloR() {
        val decorView: View = window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }


    var rateDialog: RateAppDialog? = null

    fun showDialogRate(isFinish: Boolean = false) {
        rateDialog = RateAppDialog(this, onRating = {
            prefs[AppConstants.KEY_SET_SHOW_DIALOG_RATE] = false
            val manager: ReviewManager = ReviewManagerFactory.create(this)
            val request: Task<ReviewInfo> = manager.requestReviewFlow() as Task<ReviewInfo>
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo: ReviewInfo = task.result
                    val flow: Task<Void> = manager.launchReviewFlow(
                        this, reviewInfo
                    ) as Task<Void>
                    flow.addOnSuccessListener {
                        if (isFinish) {
                            this.finishAffinity()
                            this.finish()
                        }
                    }
                } else {
                    if (isFinish) {
                        this.finishAffinity()
                        this.finish()
                    }
                }
            }
        })
        rateDialog?.show()

    }
}