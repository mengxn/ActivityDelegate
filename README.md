# Activity跳转封装
对于Activity之间的跳转进行封装统一，便于之后的管理与维护。  

## Overview
[从Activity跳转说起](从Activity跳转说起.md)  

## Example
例：打开一个Activity
```java
PIntent.from(this)
        .to(SecondActivity.class);
```
例：打开一个附带参数的Activity
```java
Bundle bundle = new Bundle();
bundle.putString("key", "hello world from last view");
PIntent.from(this)
        .with(bundle)
        .to(SecondActivity.class);
```
例：打开一个有动画切换效果的Activity
```java
PIntent.from(this)
        .transition(R.anim.slide_in_right, R.anim.slide_out_left)
        .to(SecondActivity.class);
```
例：使用共享元素动画打开 Activity
```java
PIntent.from(this)
        .with("key", "open with share view")
        .share(view, "share")
        .to(SecondActivity.class);
```
例：打开 Activity，并处理回调数据
```java
mActivityResponse = PIntent.from(this)
        .to(SecondActivity.class, REQUEST_OPEN_SECOND)
        .result(new ActivityResponse.Callback() {
            @Override
            public void onResult(Intent data) {
                Toast.makeText(MainActivity.this, data.getStringExtra("text"), Toast.LENGTH_SHORT).show();
            }
        });
```
打开[Demo](app/src/main/java/me/codego/activitydelegate/MainActivity.java)查看更多实用方法