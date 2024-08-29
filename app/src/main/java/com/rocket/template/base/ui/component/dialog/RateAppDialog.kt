package com.rocket.template.base.ui.component.dialog

import android.content.Context
import android.widget.Toast
import com.rocket.template.base.R
import com.rocket.template.base.databinding.DialogRateAppBinding
import com.rocket.template.base.ui.bases.BaseDialog
import com.rocket.template.base.ui.bases.ext.click


class RateAppDialog(context: Context, val onRating : () -> Unit) : BaseDialog<DialogRateAppBinding>(context,
    R.style.BaseDialog ) {

    override fun getLayoutDialog(): Int {
        return R.layout.dialog_rate_app
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvRateNow.click {
            dismiss()
            val star = mBinding.simpleRatingBar.rating
            if (star <= 4) {
                Toast.makeText(context, context.getString(R.string.txt_thanks_you_for_rating), Toast.LENGTH_SHORT).show()
            } else {
                onRating.invoke()
            }
        }
    }
}