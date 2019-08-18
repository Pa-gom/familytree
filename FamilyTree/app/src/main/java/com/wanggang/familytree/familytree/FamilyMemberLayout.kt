package com.wanggang.familytree.familytree

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import com.wanggang.familytree.AddOrEditMemberActivity

import com.wanggang.familytree.dp
import com.wanggang.familytree.model.FamilyDataBaseHelper
import com.wanggang.familytree.model.FamilyMemberEntity
import com.wanggang.familytree.model.FamilyMemberModel
import com.wanggang.familytree.widget.EditTreeMenuDialog
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.orhanobut.logger.Logger


class FamilyMemberLayout : ViewGroup {

    // 数据源适配器
    var familyTreeAdapter: FamilyTreeAdapter? = null

    //设置刷新监听回调（删除节点后）
    lateinit var mRefreshListener: () -> Unit

    var canMeasure = false
    var canLayout = false

    var mAnimator: ValueAnimator? = null
    var mPercent: Float = 0f
    var mPaint: Paint? = null

    private var mDialog: AlertDialog? = null
    private var mClickModel: FamilyMemberModel? = null
    private var mMenuDialog: EditTreeMenuDialog?=null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {
        mPaint = Paint()
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 1f.dp
        mPaint!!.color = Color.parseColor("#999999")
        mPaint!!.isAntiAlias = true
        setWillNotDraw(false)

        mAnimator = ValueAnimator.ofFloat(0f, 1f)
        mAnimator!!.duration = 400
        mAnimator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            mPercent = valueAnimator.animatedValue as Float
            invalidate()
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (canMeasure) {
            canMeasure = false
            for (i in 0 until childCount) {
                measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
            }
            setMeasuredDimension(familyTreeAdapter!!.realWidth, familyTreeAdapter!!.realHeight)
//            setMeasuredDimension(2000,3000)
        }

    }


//    override fun dispatchDraw(canvas: Canvas) {
//        // 获取布局控件宽高
//        val width = width
//        val height = height
//        // 创建画笔
//        val mPaint = Paint()
//        // 设置画笔的各个属性
//        mPaint.color = Color.BLUE
//        mPaint.style = Paint.Style.STROKE
//        mPaint.strokeWidth = 10f
//        mPaint.isAntiAlias = true
//        // 创建矩形框
//        val mRect = Rect(0, 0, width, height)
//        // 绘制边框
//        canvas.drawRect(mRect, mPaint)
        // 最后必须调用父类的方法
//        super.dispatchDraw(canvas)
//    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (canLayout) {
            canLayout = false
            for (i in 0 until childCount) {
                (getChildAt(i) as FamilyMemberView).layoutSelf()
                getChildAt(i).setOnClickListener(memberClick)
            }

            mAnimator!!.start()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mPercent > 0) {
            for (i in 0 until childCount) {
                (getChildAt(i) as FamilyMemberView).drawPath(canvas!!, mPaint!!, mPercent)
            }
        }

    }

    fun displayUI() {

        removeAllViews()

        val childSize = familyTreeAdapter!!.dataList!!.size
        for (i in 0 until childSize) {
            var view = FamilyMemberView(context)
            view.familyMemberModel = familyTreeAdapter!!.dataList!![i]
            addView(view)
//            addViewInLayout(view, i, LayoutParams(FamilyTreeAdapter.itemWidth, FamilyTreeAdapter.itemHeight))
        }
        canMeasure = true
        canLayout = true
        requestLayout()
    }

    fun getLeftBorder(): Int {
        return (familyTreeAdapter!!.left - 2) * (FamilyTreeAdapter.itemWidth + FamilyTreeAdapter.colSpace) / 2
    }

    fun getTopBorder(): Int {
        return (familyTreeAdapter!!.top - 1) * (FamilyTreeAdapter.itemHeight + FamilyTreeAdapter.lineSpace)
    }

    fun getRightBorder(): Int {
        return (familyTreeAdapter!!.right + 2) * (FamilyTreeAdapter.itemWidth + FamilyTreeAdapter.colSpace) / 2
    }

    fun getBottomBorder(): Int {
        return (familyTreeAdapter!!.bottom + 1) * (FamilyTreeAdapter.itemHeight + FamilyTreeAdapter.lineSpace)
    }

    @SuppressLint("CheckResult")
    private var memberClick = OnClickListener {
        mClickModel = (it as FamilyMemberView).familyMemberModel
        println("mClickModel = $mClickModel")

        mMenuDialog = EditTreeMenuDialog(context, mClickModel!!.memberEntity)
        mMenuDialog!!.show()
        mMenuDialog!!.setListener {

                when (it) {
                    0,1,2,3,4,5,6,7 -> {
                        if(it == 0 && mClickModel?.memberEntity?.fatherId != null){
                            Toast.makeText(context, "父亲节点已存在",Toast.LENGTH_SHORT).show()
                            return@setListener
                        }
                        if(it == 1 && mClickModel?.memberEntity?.fatherId == null){
                            Toast.makeText(context, "请先添加父亲节点",Toast.LENGTH_SHORT).show()
                            return@setListener
                        }else if(it == 1 && mClickModel?.memberEntity?.fatherId != null){
                            Flowable.create(FlowableOnSubscribe<Boolean> {
                                var selfFatherEntity = FamilyDataBaseHelper.getInstance(context).getFamilyMember(mClickModel?.memberEntity?.fatherId!!)
                                if (selfFatherEntity?.spouseId != null) {
                                    it.onNext(true)
                                }else{
                                    it.onNext(false)
                                }
                                it.onComplete()
                            }, BackpressureStrategy.ERROR)
                            .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    if(it) {
                                        Toast.makeText(context, "母亲节点已存在", Toast.LENGTH_SHORT).show()
                                        return@subscribe
                                    }else{
                                        openEditActivity(mClickModel?.memberEntity!!, 1)
                                    }
                                }
                            return@setListener
                        }
                        //男性和女性的夫妻节点逻辑暂时设置一样，后期考虑多个妻子节点的情况
                        if(it==3 && mClickModel?.memberEntity?.sex == 0 && mClickModel?.memberEntity?.spouseId != null){
                            Toast.makeText(context, "夫妻节点已存在",Toast.LENGTH_SHORT).show()
                            return@setListener
                        }else if(it==3 && mClickModel?.memberEntity?.sex == 1 && mClickModel?.memberEntity?.spouseId != null){
                            Toast.makeText(context, "夫妻节点已存在",Toast.LENGTH_SHORT).show()
                            return@setListener
                        }
                        openEditActivity(mClickModel?.memberEntity!!, it)
                    }
                    8 -> {
                        Flowable.create(FlowableOnSubscribe<Boolean> {

                            if(FamilyDataBaseHelper.getInstance(context).deleteMember(mClickModel?.memberEntity!!)) {
                                it.onNext(true)
                            }else{
                                it.onNext(false)
                            }
                            it.onComplete()

                        }, BackpressureStrategy.ERROR)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                if(it) {
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                    this.mRefreshListener()
                                }else{
                                    Toast.makeText(context, "只能删除边缘节点", Toast.LENGTH_SHORT).show()
                                }
                            }

                    }
                }
        }

//        if (mDialog == null) {
//            mDialog = AlertDialog.Builder(context).setItems(
//                R.array.dialog_menu
//            ) { dialog, which ->
//                dialog.dismiss()
//                if (which == 3) {
//
//                } else {
//                    val intent = Intent(context, AddOrEditMemberActivity::class.java)
//                    when (which) {
//                        0 -> intent.putExtra("id", mClickModel?.memberEntity?.id)
//                        1 -> intent.putExtra("fatherId", mClickModel?.memberEntity?.id)
//                        2 -> intent.putExtra("spouseId", mClickModel?.memberEntity?.id)
//                    }
//                    (context as Activity).startActivityForResult(intent, 111)
//                }
//            }.create()
//        }
//        mDialog?.show()
    }

    /**
     * 打开编辑或者添加界面
     */
    fun openEditActivity(familyMemberEntity: FamilyMemberEntity, type:Int){
        val intent = Intent(context, AddOrEditMemberActivity::class.java)
        intent.putExtra("id", familyMemberEntity?.id)
        intent.putExtra("fatherId", familyMemberEntity?.fatherId)
        intent.putExtra("spouseId", familyMemberEntity?.spouseId)
        intent.putExtra("sex", familyMemberEntity?.sex)
        intent.putExtra("type", type)
        (context as Activity).startActivityForResult(intent, 111)
    }

    fun setRefreshListener(listener: ()->Unit){
        this.mRefreshListener = listener
    }




}