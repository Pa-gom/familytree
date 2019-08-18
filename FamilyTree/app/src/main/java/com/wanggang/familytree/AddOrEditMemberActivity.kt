package com.wanggang.familytree

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.wanggang.familytree.model.FamilyDataBaseHelper
import com.wanggang.familytree.model.FamilyMemberEntity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_member.*

/**
 *  @author rgl
 *  @date 2019/8/17
 */
class AddOrEditMemberActivity  : AppCompatActivity() {

    //操作对象的id
    var id:Long = -1
    //操作对象父亲的id
    var fatherId:Long = -1
    //操作对象配偶的id
    var spouseId:Long = -1
    //操作对象的性别
    var sex:Int = 0
    //操作类型
    //0-添加父亲  1-添加母亲  2-添加兄弟
    //3-添加夫妻  4-添加儿子  5-添加女儿
    //6-查看   7-编辑
    var type:Int = -1
    var familyMemberEntity = FamilyMemberEntity("")


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_member)
        type = intent.getIntExtra("type", -1)
        id = intent.getLongExtra("id", -1L)
        fatherId = intent.getLongExtra("fatherId", -1L)
        spouseId = intent.getLongExtra("spouseId", -1L)
        sex = intent.getIntExtra("sex", 0)
        //查看或者编辑模式，需要重新加载操作对象的信息
        if ((type == 6 || type == 7) && id != -1L) {
            Single.create(SingleOnSubscribe<FamilyMemberEntity> {
                var familyMember = FamilyDataBaseHelper.getInstance(this).getFamilyMember(id)
                it.onSuccess(familyMember)
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    familyMemberEntity = it
                    tv_name.setText(it.name)
                    tv_shixi.setText(it.shixi)
                    tv_zibei.setText(it.zibei)
                    tv_tanghao.setText(it.tanghao)
                    sp_sex.setSelection(it.sex)
                    sp_exist.setSelection(it.exist)
                    tv_introduce.setText(it.introduce)
                    //头像问题后续再加

                }, {

                })
        }
        //查看模式下，不可点击提交
        if(type == 6){

        }
        when(type){
            0,1->{
                //添加父亲或母亲，性别限制
                sp_sex.setSelection(type)
                sp_sex.isEnabled = false
            }
            3->{
                //夫妻关系，性别相反
                sp_sex.setSelection(1-sex)
                sp_sex.isEnabled = false
            }
            4,5->{
                //添加儿子或女儿，性别限制
                sp_sex.setSelection(type-4)
                sp_sex.isEnabled = false
            }
            6->{tv_name.isEnabled = false
                tv_shixi.isEnabled = false
                tv_zibei.isEnabled = false
                tv_tanghao.isEnabled = false
                sp_sex.isEnabled = false
                sp_exist.isEnabled = false
                tv_introduce.isEnabled = false
                btn_submit.visibility= View.GONE}
        }
        initView()

    }


    private fun initView() {
        btn_back.setOnClickListener {
            finish()
        }



        btn_submit.setOnClickListener{
            if (TextUtils.isEmpty(tv_name.text.toString())){
                Toast.makeText(this,"请输入姓名",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Flowable.create(FlowableOnSubscribe<FamilyMemberEntity> {

                familyMemberEntity.name = tv_name.text!!.toString()
                familyMemberEntity.shixi = tv_shixi.text!!.toString()
                familyMemberEntity.zibei = tv_zibei.text!!.toString()
                familyMemberEntity.tanghao = tv_tanghao.text!!.toString()
                familyMemberEntity.introduce = tv_introduce.text!!.toString()

                when(sp_sex.selectedItemId){
                    0L -> familyMemberEntity.sex = 0
                    1L -> familyMemberEntity.sex = 1
                }
                when(sp_exist.selectedItemId){
                    0L -> familyMemberEntity.exist = 0
                    1L -> familyMemberEntity.exist = 1
                }
                when(type){
                    0-> {
                        //插入父亲节点
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
                        //把父亲节点的id传到原来操作对象的fatherId中
                        var selfEntity = FamilyDataBaseHelper.getInstance(this).getFamilyMember(id)
                        var selfFatherEntity = FamilyDataBaseHelper.getInstance(this).getLastInsertRootMembers(0)
                        selfEntity.fatherId = selfFatherEntity?.id
                        FamilyDataBaseHelper.getInstance(this).updateMember(selfEntity)
                    }
                    1->{
                        //添加母亲节点，依赖于父亲节点
                        familyMemberEntity.spouseId = fatherId
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
                        var selfFatherEntity = FamilyDataBaseHelper.getInstance(this).getFamilyMember(fatherId)
                        //找到刚刚插入的母亲节点
                        var selfMotherEntiry = FamilyDataBaseHelper.getInstance(this).getLastInsertRootMembers(1)
                        selfFatherEntity.spouseId = selfMotherEntiry.id
                        FamilyDataBaseHelper.getInstance(this).updateMember(selfFatherEntity)
                    }
                    2->{
                        //操作对象的父亲节点也是操作对象兄弟的父亲节点
                        familyMemberEntity?.fatherId = fatherId
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
                    }
                    3->{
                        //添加配偶,其中一方添加spouseId后，另外一方也要添加
                        familyMemberEntity?.spouseId = id
                        if(familyMemberEntity?.sex == 0){
                            //家族女性添加丈夫，fatherId设置为-1，绘制时，根据女儿显示
                            //入赘情况暂不考虑
                            familyMemberEntity?.fatherId = -1
                        }
                        //查找出操作对象的配偶id，然后添加
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
                        var selfSpouse = FamilyDataBaseHelper.getInstance(this).getSpouseMember(id)
                        var selfEntity = FamilyDataBaseHelper.getInstance(this).getFamilyMember(id)
                        selfEntity.spouseId = selfSpouse.id
                        FamilyDataBaseHelper.getInstance(this).updateMember(selfEntity)
                    }
                    4,5->{
                        //添加子女
                        familyMemberEntity?.fatherId = id
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
                    }
                    7->{
                        FamilyDataBaseHelper.getInstance(this).updateMember(familyMemberEntity!!)
                    }
                }
                it.onNext(familyMemberEntity)
                it.onComplete()

            }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK)
                    finish()

                }
        }
    }


}