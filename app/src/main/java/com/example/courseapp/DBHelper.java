package com.example.courseapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 专门管数据库的工具类，建库、建表都放这里。
 * 报告 3.1 里说的那个 DBHelper 就是这个。
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context ctx) {
        // 库文件叫 course.db，版本先写 1
        super(ctx, "course.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表：账号、密码
        db.execSQL("CREATE TABLE user ("
                + " account TEXT PRIMARY KEY,"
                + " password TEXT NOT NULL)");

        // 课程表：课程编号、课程名、任课老师、上课地点（没有学分了）
        db.execSQL("CREATE TABLE course ("
                + " course_no TEXT PRIMARY KEY,"
                + " course_name TEXT NOT NULL,"
                + " teacher TEXT,"
                + " place TEXT)");

        // 第一次建库先塞两个账号，不然没账号可登
        db.execSQL("INSERT INTO user (account, password) VALUES ('admin', '123456')");
        db.execSQL("INSERT INTO user (account, password) VALUES ('stu', '123456')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 课设就这一版，升级直接重建，数据丢了也无所谓
        db.execSQL("DROP TABLE IF EXISTS course");
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }
}
