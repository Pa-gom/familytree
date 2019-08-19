package com.wanggang.family;

import android.util.Log;
import com.wanggang.familytree.model.FamilyDataBaseHelper;
import com.wanggang.familytree.model.FamilyMemberEntity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @author rgl
 * @date 2019/8/19
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 应该在application或者service中初始化，这样才能保证添加或修改节点后，可以及时反馈到服务器
         */
        initFamilyTreeDataListener();
    }

    private void initFamilyTreeDataListener(){

        FamilyDataBaseHelper.Companion.setDataDeleteListener(new Function1<FamilyMemberEntity, Unit>() {
            @Override
            public Unit invoke(FamilyMemberEntity familyMemberEntity) {
                Log.d("FamilyTree","删除节点"+familyMemberEntity.getName());
                return null;
            }
        });

        FamilyDataBaseHelper.Companion.setDataInsertListener(new Function1<FamilyMemberEntity, Unit>() {
            @Override
            public Unit invoke(FamilyMemberEntity familyMemberEntity) {
                Log.d("FamilyTree","插入节点"+familyMemberEntity.getName());
                return null;
            }
        });

        FamilyDataBaseHelper.Companion.setDataUpdateListener(new Function1<FamilyMemberEntity, Unit>() {
            @Override
            public Unit invoke(FamilyMemberEntity familyMemberEntity) {
                Log.d("FamilyTree","修改节点"+familyMemberEntity.getName());
                return null;
            }
        });
    }
}
