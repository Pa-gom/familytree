package com.wanggang.familytree.model

import android.arch.persistence.room.Room
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.wanggang.familytree.MainActivity

class FamilyDataBaseHelper constructor(context: Context) {

    private val appDataBase = Room.databaseBuilder(context, FamilyDataBase::class.java,
        "family.db").build()!!



    companion object {
        @Volatile
        var INSTANCE: FamilyDataBaseHelper? = null
        @Volatile
        var OnDataInsertListener: ((FamilyMemberEntity) -> Unit)? = null
        @Volatile
        var OnDataDeleteListener: ((FamilyMemberEntity) -> Unit)? = null
        @Volatile
        var OnDataUpdateListener: ((FamilyMemberEntity) -> Unit)? = null

        fun getInstance(context: Context): FamilyDataBaseHelper {
            if (INSTANCE == null) {
                synchronized(FamilyDataBaseHelper::class) {
                    if (INSTANCE == null) {
                        INSTANCE = FamilyDataBaseHelper(context.applicationContext)
                    }
                }
            }
            return INSTANCE!!
        }


        fun setDataInsertListener(listener: (FamilyMemberEntity) -> Unit){
            synchronized(FamilyDataBaseHelper::class) {
                this.OnDataInsertListener = listener
            }
        }
        fun setDataDeleteListener(listener: (FamilyMemberEntity) -> Unit){
            synchronized(FamilyDataBaseHelper::class) {
                this.OnDataDeleteListener = listener
            }
        }
        fun setDataUpdateListener(listener: (FamilyMemberEntity) -> Unit){
            synchronized(FamilyDataBaseHelper::class) {
                this.OnDataUpdateListener = listener
            }
        }

    }

    /**
     * 根据id获取Member
     */
    fun getFamilyMember(id: Long): FamilyMemberEntity{
        return appDataBase.familyMemberDao().getMemberById(id)
    }

    /**
     * 查询最新插入的root节点（最新插入，没有返回id），查询父亲或母亲节点
     * 性别为男，没有父亲节点的最新插入的是根节点，即祖先（因为不能在中间节点添加父亲）
     *     例外： 女儿节点，新增丈夫后也是没有父亲节点（加载显示时为避免出问题，把这一类节点新增时fatherId设置为-1即可）
     * 母亲节点随时可能在中间插入节点
     */
    fun getLastInsertRootMembers(sex: Int):FamilyMemberEntity{
        return appDataBase.familyMemberDao().getLastInsertRootMembers(sex)
    }

    /**
     * 根据id获取配偶Member
     */
    fun getSpouseMember(id: Long): FamilyMemberEntity{
        return appDataBase.familyMemberDao().getSpouseMemberById(id)
    }

    /**
     * 根据id获取子Member
     */
    fun getChildMembers(id: Long): List<FamilyMemberEntity> {
        return appDataBase.familyMemberDao().getChildMembers(id)
    }

    /**
     * 更新FamilyMemberEntity;必须在非主线程中进行
     */
    fun updateMember(member: FamilyMemberEntity) {
        OnDataUpdateListener!!(member)
        appDataBase.familyMemberDao().updateItem(member)
    }

    /**
     * 插入FamilyMemberEntity;必须在非主线程中进行
     */
    fun insertMember(member: FamilyMemberEntity) {
        OnDataInsertListener!!(member)
        appDataBase.familyMemberDao().insertItem(member)
    }

    /**
     * 删除节点，只能删除没有子节点的终端节点;必须在非主线程中进行
     */
    fun deleteMember(member: FamilyMemberEntity):Boolean{
        var memberList = getChildMembers(member?.id)
        if(memberList?.size > 0){
            return false
        }else{
            OnDataDeleteListener!!(member)
            appDataBase.familyMemberDao().deleteItem(member)
            return true
        }
    }
}