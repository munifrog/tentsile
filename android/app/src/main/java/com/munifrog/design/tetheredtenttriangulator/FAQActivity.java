package com.munifrog.design.tetheredtenttriangulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class FAQActivity extends AppCompatActivity {
    private VersionSupport mVersionSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        mVersionSupport = new VersionSupport(this);
        mVersionSupport.hideVirtualButtons();

        RecyclerView rv = findViewById(R.id.rv_question_and_answer);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        FAQAdapter adapter = new FAQAdapter(
                getResources().getStringArray(R.array.faq_questions),
                getResources().getStringArray(R.array.faq_answers)
        );
        rv.setAdapter(adapter);
    }
}