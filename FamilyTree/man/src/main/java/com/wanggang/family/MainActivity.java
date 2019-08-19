package com.wanggang.family;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.wanggang.familytree.Main3Activity;
import com.wanggang.familytree.model.FamilyDataBaseHelper;
import com.wanggang.familytree.model.FamilyMemberEntity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, Main3Activity.class));

    }
}
