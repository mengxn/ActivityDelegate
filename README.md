# Activity跳转封装
对于Activity之间的跳转进行封装统一，便于之后的管理与维护。  

例：打开一个Activity
```java
ActivityDelegate.create(SomeActivity.class).open(context);
```
例：打开一个附带参数的Activity
```java
Bundle bundle = new Bundle();
bundle.putString("key", "hello world from last view");
ActivityDelegate.create(SecondActivity.class, bundle).open(this);
```
例：打开一个有动画切换效果的Activity
```java
ActivityDelegate.create(SecondActivity.class)
                .transition(R.anim.slide_in_right, R.anim.slide_out_left)
                .open(this);
```
例：结束一个Activity
```java
ActivityDelegate.create(this).close();
```
例：结束一个有动画切换效果的Activity
```java
ActivityDelegate.create(this)
        .transition(R.anim.slide_in_left, R.anim.slide_out_right)
        .close();
```