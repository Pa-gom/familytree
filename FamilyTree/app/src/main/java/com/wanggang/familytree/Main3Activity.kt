package com.wanggang.familytree

import android.app.Activity
import android.content.Intent
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wanggang.familytree.familytree.FamilyTreeAdapter
import com.wanggang.familytree.model.FamilyDataBaseHelper
import com.wanggang.familytree.model.FamilyMemberEntity
import com.wanggang.familytree.model.FamilyMemberModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main3.*
import android.widget.RelativeLayout
import android.widget.TextView
import com.wanggang.familytree.widget.LevelLabelView


class Main3Activity : AppCompatActivity() {

    val TAG = "FamilyMemberEntity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        initView()

    }

    private fun initView() {
        familyMemberLayout.familyTreeAdapter = FamilyTreeAdapter()
        initData()

        //删除节点后，需要重绘
        familyMemberLayout.setRefreshListener {
            initData()
        }
    }

    private fun initData() {

        Flowable.create(FlowableOnSubscribe<FamilyMemberModel> {

            // sharePreference存储firstIn，标记当前是否第一次启动
            var firstIn by Preference<String>("firstIn", "1", this)
            println("firstIn = $firstIn")
            if ("1" == firstIn) {

                // 应用第一次启动，则往数据库插入一些数据

                // 插入第一条数据，同时也是整个家族树的跟节点
                var familyMember = FamilyMemberEntity("王根")
                familyMember.imagePath = "http://www.qqpk.cn/Article/UploadFiles/201202/20120219130832435.jpg"
                familyMember.phone = "18156094171"
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王明")
                familyMember.imagePath = "http://www.qqpk.cn/Article/UploadFiles/201202/20120219130832435.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王芸")
                familyMember.imagePath = "http://www.qqpk.cn/Article/UploadFiles/201202/20120219130832435.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王恩")
                familyMember.imagePath = "http://www.qqpk.cn/Article/UploadFiles/201202/20120219130832435.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 2 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                firstIn = "0"
            }

            var familyMember = FamilyDataBaseHelper.getInstance(this).getLastInsertRootMembers(0)

            if (familyMember != null) {
                var familyMemberModel = familyMember.generateMember(this, 0)
                it.onNext(familyMemberModel)
            } else {
                it.onError(Throwable("空的"))
            }
            it.onComplete()

        }, BackpressureStrategy.ERROR)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                familyMemberLayout.familyTreeAdapter!!.dealWithData(it) {
                    familyMemberLayout.displayUI()
                    val params = familyMemberLayout.layoutParams
                    params.width = familyMemberLayout.getRightBorder()*2+800
                    params.height = familyMemberLayout.getBottomBorder()+420
                    if(params.width < resources.displayMetrics.widthPixels){
                        params.width = resources.displayMetrics.widthPixels
                    }
                    if(params.height < resources.displayMetrics.heightPixels){
                        params.height = resources.displayMetrics.heightPixels
                    }
                    familyMemberLayout.layoutParams = params
                    setLevelLable(it)
                }
            }

    }

    fun setLevelLable(familyMemberModel: FamilyMemberModel){
        var level:Int = getLevel(familyMemberModel)
        containerLeft.removeAllViews()
        containerRight.removeAllViews()
        for(i in 1..level){
            var textViewLeft = LevelLabelView(this)
            textViewLeft.setViewStyle("第"+i+"代",10,(i-1)*350,0,0)
            containerLeft.addView(textViewLeft)
            var textViewRight = LevelLabelView(this)
            textViewRight.setViewStyle("第"+i+"代",10,(i-1)*350,0,0)
            containerRight.addView(textViewRight)
        }



    }

    fun getLevel(familyMemberModel: FamilyMemberModel):Int{

        recursionGetLevel(familyMemberModel, 1)
        return num
    }

    var num = 0

    fun recursionGetLevel(familyMemberModel: FamilyMemberModel,k:Int) {
        if(k == 1){
            num = 1
        }
        num = Math.max(num,k)
        if (familyMemberModel.childModels != null)
            for(item in familyMemberModel?.childModels!!){
                recursionGetLevel(item,k+1)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            initData()
        }
    }
}
