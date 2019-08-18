package com.wanggang.familytree.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.wanggang.familytree.R
import org.w3c.dom.Text

/**
 *  @author rgl
 *  @date 2019/8/18
 */
class LevelLabelView : TextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    fun setViewStyle(content:String, x:Int,y:Int,w:Int,h:Int){
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT)
        params.topMargin = 280+y
        setEms(1)
        params.rightMargin = x
        setPadding(2,8,2,8)
        layoutParams = params
        text = content
    }
}