# Activity跳转封装
对于Activity之间的跳转进行封装统一，便于之后的管理与维护。采用链式调用，使用更方便。  

## Overview
[从Activity跳转说起](从Activity跳转说起.md) 

## Usage
Gradle:
```groovy
# project build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

# module build.gralde
dependencies {
    ...
    implementation 'com.github.mengxn:ActivityDelegate:1.0.4'
}
``` 

## Example

例：打开一个Activity
```java
PIntent.from(this)
        .to(SecondActivity.class);
```

例：打开一个附带参数的Activity，包括基础类型int、String等，或者直接传递bundle数据
```java
Bundle bundle = new Bundle();
bundle.putString("key", "hello world from last view");
PIntent.from(this)
        .with("num", 1)
        .with("isFirst", true)
        .with(bundle)
        .to(SecondActivity.class);
```

例：打开 Activity，并处理回调数据
```java
PIntent.from(this)
        .to(SecondActivity.class, REQUEST_OPEN_SECOND)
        .result(new IRequest.Callback() {
            @Override
            public void onResult(Intent data) {
                // do something when activity callback
                Toast.makeText(MainActivity.this, data.getStringExtra("text"), Toast.LENGTH_SHORT).show();
            }
        });
```

例：打开一个有动画切换效果的Activity
```java
PIntent.from(this)
        .transition(R.anim.slide_in_right, R.anim.slide_out_left)
        .to(SecondActivity.class);
```

例：使用共享元素动画打开 Activity
```java
// 打开新的Activity
PIntent.from(this)
        .with("key", "open with share view")
        .share(view, "share")
        .to(SecondActivity.class);

// 在新界面执行共享元素动画，在 setContentView 之前设置
PIntent.postStartTransition(this, this);
setContentView(layoutId);

```

打开[Demo](app/src/main/java/me/codego/activitydelegate/MainActivity.java)查看更多实用方法
