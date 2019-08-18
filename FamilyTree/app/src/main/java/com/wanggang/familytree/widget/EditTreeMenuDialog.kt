package com.wanggang.familytree.widget

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import com.wanggang.familytree.R
import com.wanggang.familytree.model.FamilyMemberEntity
import com.wanggang.familytree.model.FamilyMemberModel
import kotlinx.android.synthetic.main.view_edit_tree_menu.*

/**
 *  @author rgl
 *  @date 2019/8/18
 */
class EditTreeMenuDialog :AlertDialog{
    var familyMemberEntity:FamilyMemberEntity?=null
    //设置一个按钮点击监听，返回各个按钮的代码
    //0-添加父亲  1-添加母亲  2-添加兄弟
    //3-添加夫妻  4-添加儿子  5-添加女儿
    //6-查看   7-编辑  8-删除
    lateinit var mBtnClickListener: (Int) -> Unit


    constructor(context: Context?, familyMemberEntity: FamilyMemberEntity?) : super(context) {
        this.familyMemberEntity = familyMemberEntity
    }

    constructor(
        context: Context?,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?,
        familyMemberEntity: FamilyMemberEntity?
    ) : super(context, cancelable, cancelListener) {
        this.familyMemberEntity = familyMemberEntity
    }

    constructor(context: Context?, themeResId: Int, familyMemberEntity: FamilyMemberEntity?) : super(
        context,
        themeResId
    ) {
        this.familyMemberEntity = familyMemberEntity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_edit_tree_menu)
        initView()
    }

    private fun initView(){
        tv_name.setText("姓名："+familyMemberEntity!!.name)
        when(familyMemberEntity!!.sex){
            0-> tv_sex.setText("性别：男")
            1-> tv_sex.setText("性别：女")
        }
        btn_add_father.setOnClickListener {
            this.mBtnClickListener(0)
            dismiss()
        }
        btn_add_mother.setOnClickListener {
            this.mBtnClickListener(1)
            dismiss()
        }
        btn_add_brother.setOnClickListener {
            this.mBtnClickListener(2)
            dismiss()
        }
        btn_add_spouse.setOnClickListener {
            this.mBtnClickListener(3)
            dismiss()
        }
        btn_add_son.setOnClickListener {
            this.mBtnClickListener(4)
            dismiss()
        }
        btn_add_daughter.setOnClickListener {
            this.mBtnClickListener(5)
            dismiss()
        }
        btn_watch.setOnClickListener {
            this.mBtnClickListener(6)
            dismiss()
        }
        btn_edit.setOnClickListener {
            this.mBtnClickListener(7)
            dismiss()
        }
        btn_delete.setOnClickListener {
            this.mBtnClickListener(8)
            dismiss()
        }
    }

    public fun setListener(listener: (Int) -> Unit){
        this.mBtnClickListener = listener
    }

}