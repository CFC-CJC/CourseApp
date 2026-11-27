# 课程信息管理 APP（Android 工程源码）

跟课设报告里贴的代码完全一致，只是把报告里"靠文字说明"的部分（DBHelper 建表、onResume 刷新、update 修改）补成了真代码，所以这个工程是能直接跑起来的。

## 怎么跑起来

1. 把 `CourseApp` 整个文件夹拷到电脑上（路径别带中文，虽然带中文一般也能开）。
2. 打开 Android Studio → `File` → `Open` → 选中 `CourseApp` 文件夹 → `Trust Project`。
3. 等它自己 Sync（第一次会下载 Gradle 8.7 和 AGP 8.5.2，网慢的话要多等一会儿）。
4. 上面选一台模拟器或者插上手机（手机要开 USB 调试），点绿色三角 Run。

如果 Android Studio 版本比较新，弹窗提示"upgrade AGP"，点同意让它升就行，代码不用改。

我这台电脑上没装 Android SDK，所以没能帮你编译验证，工程是按标准结构搭的。

## 登录账号

- 管理员（能增删改）：`admin` / `123456`
- 普通学生：`stu` / `123456`

两个账号是 `DBHelper.onCreate()` 建库的时候插进去的，不用手动加。

## 目录结构

```
CourseApp/
├── settings.gradle / build.gradle / gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/courseapp/
        │   ├── LoginActivity.java    登录页
        │   ├── MainActivity.java     主界面：列表 + 增删改查按钮 + 搜索
        │   ├── AddActivity.java      添加和修改共用页
        │   └── DBHelper.java         建库建表、塞默认账号
        └── res/layout/
            ├── activity_login.xml
            ├── activity_main.xml
            ├── activity_add.xml
            └── item_course.xml       列表每一行的样子（4 个 TextView）
```

## 跟报告对应的地方

- 报告 3.2.1 登录那段 → `LoginActivity.onCreate()` 里的 `btnLogin.setOnClickListener`
- 报告 3.2.2 添加那段 → `AddActivity.addCourse()`
- 报告 3.2.3 列表那段 → `MainActivity.showList(String key)`
- 报告 4.2 说的"读数据挪到 onResume" → `MainActivity.onResume()`
- 报告 3.2 末尾说的修改/删除思路 → `AddActivity.updateCourse()`、`MainActivity` 里 `btnDel`

## 和报告不一样的两处（故意的）

1. `addCourse()` 只负责新增；修改单独写了 `updateCourse()`，用 `db.update(..., "course_no=?", ...)`。报告里也是这么说的，只是报告没贴代码。
2. 加了异常保护吗？没有。跟报告里写的一样，还是新手写法，`cur.close()` 只在正常路径关，课设数据量小，不影响用。

## 已验证/未验证

- 已验证：所有 XML 能正常解析，4 个 Java 文件括号配平，布局里的 id 和代码里 `R.id` 一一对应。
- 未验证：真机编译运行（本机没有 Android SDK）。
