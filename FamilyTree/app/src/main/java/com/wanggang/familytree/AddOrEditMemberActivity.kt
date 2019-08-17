package com.wanggang.familytree

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.wanggang.familytree.model.FamilyDataBaseHelper
import com.wanggang.familytree.model.FamilyMemberEntity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_member.*
import kotlinx.android.synthetic.main.activity_main2.*

/**
 *  @author rgl
 *  @date 2019/8/17
 */
class AddOrEditMemberActivity  : AppCompatActivity() {

    var exist : Boolean = true
    var sex : Int = 0
    var id:Long = 0
    var fatherId:Long = -1
    var spouseId:Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_member)
        id = intent.getLongExtra("id", -1L)
        fatherId = intent.getLongExtra("fatherId", -1L)
        spouseId = intent.getLongExtra("spouseId", -1L)
        initView()

    }


    private fun initView() {
        btn_back.setOnClickListener {
            finish()
        }

        rb_sex.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId) {
                0 -> sex = 0
                1 -> sex = 1
            }
        }

        rb_exist.setOnCheckedChangeListener{group, checkedId ->
            when (checkedId) {
                0 -> exist = true
                1 -> exist = false
            }
        }

        btn_submit.setOnClickListener{
            if (TextUtils.isEmpty(tv_name.text.toString())){
                Toast.makeText(this,"请输入姓名",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Flowable.create(FlowableOnSubscribe<FamilyMemberEntity> {

                var familyMemberEntity = FamilyMemberEntity(tv_name.text.toString())
                familyMemberEntity.shixi = tv_shixi.text.toString()
                familyMemberEntity.zibei = tv_zibei.text.toString()
                familyMemberEntity.tanghao = tv_tanghao.text.toString()
                familyMemberEntity.sex = sex
                familyMemberEntity.exist = exist
                familyMemberEntity.introduce = tv_introduce.text.toString()

                when {
                    id != -1L -> {
                        familyMemberEntity!!.id = id
                        FamilyDataBaseHelper.getInstance(this).updateMember(familyMemberEntity!!)
                    }
                    fatherId != -1L -> {
                        familyMemberEntity!!.fatherId = fatherId
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
                    }
                    spouseId != -1L -> {
                        familyMemberEntity!!.spouseId = spouseId
                        FamilyDataBaseHelper.getInstance(this).insertMember(familyMemberEntity!!)
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