package com.example.courseapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面：上面搜索框，中间课程列表，下面一排按钮。
 * showList() 就是报告 3.2.3 贴的那段。
 */
public class MainActivity extends Activity {

    EditText etKey;
    ListView lvCourse;
    Button btnSearch, btnAdd, btnEdit, btnDel, btnExit;

    // 当前在列表里点中的那条课程编号
    String selCno = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etKey = (EditText) findViewById(R.id.etKey);
        lvCourse = (ListView) findViewById(R.id.lvCourse);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDel = (Button) findViewById(R.id.btnDel);
        btnExit = (Button) findViewById(R.id.btnExit);

        // 点列表某一行，记住是哪门课
        lvCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Map item = (Map) parent.getItemAtPosition(pos);
                selCno = (String) item.get("cno");
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList(etKey.getText().toString().trim());
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, AddActivity.class);
                startActivity(it);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selCno == null) {
                    Toast.makeText(MainActivity.this, "先在列表里点一门课",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent it = new Intent(MainActivity.this, AddActivity.class);
                it.putExtra("cno", selCno);
                startActivity(it);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selCno == null) {
                    Toast.makeText(MainActivity.this, "先在列表里点一门课",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除课程")
                        .setMessage("确定要删掉课程 " + selCno + " 吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper h = new DBHelper(MainActivity.this);
                                SQLiteDatabase db = h.getWritableDatabase();
                                db.delete("course", "course_no=?", new String[]{selCno});
                                db.close();
                                selCno = null;
                                Toast.makeText(MainActivity.this, "删掉了",
                                        Toast.LENGTH_SHORT).show();
                                showList(etKey.getText().toString().trim());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 报告 4.2 里踩的那个坑：读数据放在 onResume，别只放 onCreate
    @Override
    protected void onResume() {
        super.onResume();
        showList(etKey.getText().toString().trim());
    }

    private void showList(String key) {
        DBHelper h = new DBHelper(this);
        SQLiteDatabase db = h.getReadableDatabase();
        Cursor cur;
        if (key.length() == 0) {
            cur = db.rawQuery("select * from course order by course_no", null);
        } else {
            cur = db.rawQuery("select * from course where course_name like ?",
                    new String[]{"%" + key + "%"});
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (cur.moveToNext()) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("cno", cur.getString(cur.getColumnIndex("course_no")));
            item.put("cname", cur.getString(cur.getColumnIndex("course_name")));
            item.put("tea", cur.getString(cur.getColumnIndex("teacher")));
            item.put("place", cur.getString(cur.getColumnIndex("place")));
            list.add(item);
        }
        cur.close();
        db.close();
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.item_course,
                new String[]{"cno", "cname", "tea", "place"},
                new int[]{R.id.item_no, R.id.item_name, R.id.item_teacher,
                        R.id.item_place});
        lvCourse.setAdapter(adapter);
    }
}
