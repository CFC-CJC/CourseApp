package com.example.courseapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加和修改共用这一个页面：从主界面直接进来是"添加"，
 * 带着课程编号进来就是"修改"。
 * addCourse() 就是报告 3.2.2 贴的那段。
 */
public class AddActivity extends Activity {

    EditText etNo, etName, etTeacher, etPlace;
    Spinner spWeek, spJie;
    TextView tvTimes;
    Button btnAddTime, btnClearTime;
    Button btnSave, btnBack;

    // 星期几和第几节就这几种，直接写死在代码里，省得再建资源文件
    String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    String[] jies  = {"上午第1节", "上午第2节", "下午第1节", "下午第2节", "晚上第1节"};

    // 一门课一周可能上好几次，时间先用 List 攒着，保存的时候拼成一个字符串存进库
    List<String> timeList = new ArrayList<String>();

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
        spWeek = (Spinner) findViewById(R.id.spWeek);
        spJie = (Spinner) findViewById(R.id.spJie);
        tvTimes = (TextView) findViewById(R.id.tvTimes);
        btnAddTime = (Button) findViewById(R.id.btnAddTime);
        btnClearTime = (Button) findViewById(R.id.btnClearTime);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);

        // 两个下拉框的内容，用系统自带的那种下拉样式就行，不折腾
        ArrayAdapter<String> adWeek = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, weeks);
        spWeek.setAdapter(adWeek);
        ArrayAdapter<String> adJie = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, jies);
        spJie.setAdapter(adJie);
        showTimes();

        Intent it = getIntent();
        oldCno = it.getStringExtra("cno");
        if (oldCno != null) {
            fillOld(oldCno);
            setTitle("修改课程");
        } else {
            setTitle("添加课程");
        }

        // 点“加到时间里”，就把下拉框选中的那一条塞进 List，可以反复加
        btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = spWeek.getSelectedItem().toString();
                String j = spJie.getSelectedItem().toString();
                String one = w + j;      // 比如 "周一下午第1节"
                if (timeList.contains(one)) {
                    Toast.makeText(AddActivity.this, "这个时间已经加过了",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                timeList.add(one);
                showTimes();
            }
        });

        // 加错了就清掉重新选，反正就那么几条
        btnClearTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeList.clear();
                showTimes();
            }
        });

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

    // 把 List 里的时间用顿号连起来，存进库；一个都没选就是空字符串
    private String joinTimes() {
        String s = "";
        for (int i = 0; i < timeList.size(); i++) {
            if (i > 0) {
                s = s + "、";
            }
            s = s + timeList.get(i);
        }
        return s;
    }

    // 下面那行小字显示已经加了哪些时间
    private void showTimes() {
        if (timeList.size() == 0) {
            tvTimes.setText("还没有加时间，可以不加");
            return;
        }
        tvTimes.setText(joinTimes());
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
            // 库里存的是 "周一下午第1节、周三上午第2节" 这种，按顿号拆开再塞回 List
            String t = cur.getString(cur.getColumnIndex("time"));
            timeList.clear();
            if (t != null && t.length() > 0) {
                String[] arr = t.split("、");
                for (int i = 0; i < arr.length; i++) {
                    timeList.add(arr[i]);
                }
            }
            showTimes();
        }
        cur.close();
        db.close();
    }

    private void addCourse() {
        String cno    = etNo.getText().toString().trim();
        String cname  = etName.getText().toString().trim();
        String tea    = etTeacher.getText().toString().trim();
        String place  = etPlace.getText().toString().trim();
        String ctime  = joinTimes();
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
        cv.put("time", ctime);
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
        String ctime  = joinTimes();
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
        cv.put("time", ctime);
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
