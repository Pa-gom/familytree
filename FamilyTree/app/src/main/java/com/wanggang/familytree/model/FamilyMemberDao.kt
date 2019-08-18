package com.wanggang.familytree.model

import android.arch.persistence.room.*


@Dao
interface BaseDao<T> {

    @Insert
    fun insertItem(item: T) //插入单条数据

    @Insert
    fun insertItems(items: List<T>) //插入list数据

    @Delete
    fun deleteItem(item: T) //删除item

    @Update
    fun updateItem(item: T) //更新item
}

@Dao
interface FamilyMemberDao: BaseDao<FamilyMemberEntity> {

    /**
     * 根据id查询FamilyMemberEntity
     */
    @Query("SELECT * FROM members WHERE id = :id ")
    fun getMemberById(id: Long): FamilyMemberEntity

    /**
     * 根据id查询配偶信息
     */
    @Query("SELECT * FROM members WHERE spouseId = :spouseId order by id desc")
    fun getSpouseMemberById(spouseId: Long): FamilyMemberEntity

    /**
     * 根据id查询所有子FamilyMemberEntity集合
     */
    @Query("SELECT * FROM members WHERE fatherId = :id ")
    fun getChildMembers(id: Long): List<FamilyMemberEntity>

    /**
     * 查询全部结果
     */
    @Query("SELECT * FROM members")
    fun getAllMembers(): List<FamilyMemberEntity>

    /**
     * 查询最新插入的root节点（最新插入，没有返回id），查询父亲或母亲节点
     * 性别为男，没有父亲节点的最新插入的是根节点，即祖先（因为不能在中间节点添加父亲）
     *     例外： 女儿节点，新增丈夫后也是没有父亲节点（加载显示时为避免出问题，把这一类节点新增时fatherId设置为-1即可）
     * 母亲节点随时可能在中间插入节点
     */
    @Query("SELECT * FROM members WHERE fatherId is null and sex = :sex order by id desc limit 1")
    fun getLastInsertRootMembers(sex: Int):FamilyMemberEntity
}