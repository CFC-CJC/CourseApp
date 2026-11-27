package com.example.courseapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 添加和修改共用这一个页面：从主界面直接进来是"添加"，
 * 带着课程编号进来就是"修改"。
 * addCourse() 就是报告 3.2.2 贴的那段。
 */
public class AddActivity extends Activity {

    EditText etNo, etName, etTeacher, etPlace;
    Button btnSave, btnBack;

    // 从主界面带过来的课程编号，为 null 表示这次是新增
    String oldCno = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etNo = (EditText) findViewById(R.id.etNo);
        etName = (EditText) findViewById(R.id.etName);
        etTeacher = (EditText) findViewById(R.id.etTeacher);
        etPlace = (EditText) findViewById(R.id.etPlace);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);

        Intent it = getIntent();
        oldCno = it.getStringExtra("cno");
        if (oldCno != null) {
            fillOld(oldCno);
            setTitle("修改课程");
        } else {
            setTitle("添加课程");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldCno == null) {
                    addCourse();
                } else {
                    updateCourse();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 修改的时候先把原来的数据读出来填进框里
    private void fillOld(String cno) {
        DBHelper h = new DBHelper(this);
        SQLiteDatabase db = h.getReadableDatabase();
        Cursor cur = db.rawQuery("select * from course where course_no=?",
                new String[]{cno});
        if (cur.moveToNext()) {
            etNo.setText(cur.getString(cur.getColumnIndex("course_no")));
            etName.setText(cur.getString(cur.getColumnIndex("course_name")));
            etTeacher.setText(cur.getString(cur.getColumnIndex("teacher")));
            etPlace.setText(cur.getString(cur.getColumnIndex("place")));
        }
        cur.close();
        db.close();
    }

    private void addCourse() {
        String cno    = etNo.getText().toString().trim();
        String cname  = etName.getText().toString().trim();
        String tea    = etTeacher.getText().toString().trim();
        String place  = etPlace.getText().toString().trim();
        if (cno.length() == 0 || cname.length() == 0) {
            Toast.makeText(this, "课程编号和课程名不能空着",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        DBHelper h = new DBHelper(this);
        SQLiteDatabase db = h.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("course_no", cno);
        cv.put("course_name", cname);
        cv.put("teacher", tea);
        cv.put("place", place);
        long res = db.insert("course", null, cv);   // 失败会返回 -1
        if (res == -1) {
            Toast.makeText(this, "添加失败，编号可能重复了",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        db.close();
    }

    // 修改走 update，不能再用 insert，不然就变成又加了一条
    private void updateCourse() {
        String cno    = etNo.getText().toString().trim();
        String cname  = etName.getText().toString().trim();
        String tea    = etTeacher.getText().toString().trim();
        String place  = etPlace.getText().toString().trim();
        if (cno.length() == 0 || cname.length() == 0) {
            Toast.makeText(this, "课程编号和课程名不能空着",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        DBHelper h = new DBHelper(this);
        SQLiteDatabase db = h.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("course_no", cno);
        cv.put("course_name", cname);
        cv.put("teacher", tea);
        cv.put("place", place);
        int n = db.update("course", cv, "course_no=?", new String[]{oldCno});
        if (n <= 0) {
            Toast.makeText(this, "没找到这门课，改不了", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "改好了", Toast.LENGTH_SHORT).show();
            finish();
        }
        db.close();
    }
}
