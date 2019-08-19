package com.wanggang.familytree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.wanggang.familytree.model.FamilyDataBaseHelper;
import com.wanggang.familytree.model.FamilyMemberEntity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @author rgl
 * @date 2019/8/19
 */
public class MainAc extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, Main3Activity.class));
        FamilyDataBaseHelper.Companion.setOnDataDeleteListener(new Function1<FamilyMemberEntity, Unit>() {
            @Override
            public Unit invoke(FamilyMemberEntity familyMemberEntity) {
                return null;
            }
        });
    }
}
