package com.jw.library.utils

import android.support.annotation.DrawableRes
import android.widget.ImageView

import com.bumptech.glide.Glide


/**
 * Created by liyuan on 16/11/6.
 */

object ImageViewUtil {
    val DEFAULT_PRESSED_ALPHA = 0.5f
    val OPTION_ANIM = createBuilder().setAnima(true).build()
    val OPTION_DEFAULT = createBuilder().build()
    val OPTION_ROUND = createBuilder().setRound(true).build()
    val OPTION_ROUND_HEAD_IMG = createBuilder()
        .setRound(true)
        .build()

    fun createBuilder(): OptionBuilder {
        return OptionBuilder()
    }

    @JvmOverloads
    fun setImageUriAsync(iv: ImageView, uri: String, option: Option = OPTION_DEFAULT) {
        Glide.with(iv.context).load(uri).into(iv)
    }


    /**
     * 设置圆形icon
     */
    fun setImageUriRoundAsync(iv: ImageView, uri: String) {
        setImageUriAsync(iv, uri, OPTION_ROUND)
    }

    class Option(private val builder: OptionBuilder) {

        val failRes: Int
            @DrawableRes
            get() = builder.failRes

        val loadingRes: Int
            @DrawableRes
            get() = builder.loadingRes

        val isAnima: Boolean
            get() = builder.anima

        val isRound: Boolean
            get() = builder.round
    }

    class OptionBuilder {
        var anima = false
        @DrawableRes
        var failRes = 0
        @DrawableRes
        var loadingRes = 0
        var round = false

        fun build(): Option {
            return Option(this)
        }

        fun setAnima(anima: Boolean): OptionBuilder {
            this.anima = anima
            return this
        }

        fun setFailRes(failRes: Int): OptionBuilder {
            this.failRes = failRes
            return this
        }

        fun setLoadingRes(loadingRes: Int): OptionBuilder {
            this.loadingRes = loadingRes
            return this
        }

        fun setRound(round: Boolean): OptionBuilder {
            this.round = round
            return this
        }
    }
}