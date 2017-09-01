### 从Activity跳转说起
Activity之间的跳转，我们一般这样写
```java
startActivity(new Intent(MainActivity.this, SecondActivity.class));
```
如果需要附带参数的话
```java
Intent intent = new Intent(MainActivity.this, SecondActivity.class);
intent.putExtra("extra", "Hello World");
startActivity(intent);
```
我们能不能简化一些呢？比如，像这样
```java
PIntent.from(MainActivity.this).to(SecondActivity.class);
```
附带参数
```java
PIntent.from(MainActivity.this)
        .with("extra", "Hello World")
        .to(SecondActivity.class);
```
这样是不是更加的通俗易懂，更容易理解Intent的作用呢？

我们再聊点其他的事情。  
当我打算跳转BActivity的时候，BActivity需要什么参数，我们是不知道。难道我们要打开BActivity查看需要什么参数吗？这样太不友好了。我们可以像这样在BActivity中提供一个static方法，用于打开本身。
```java
public static void open(Context context, String id) {
    PIntent.from(context)
            .with("id", id)
            .to(BActivity.class);
}
```
我们在需要打开BActivity的时候就可以直接这样调用
```java
BActivity.open(MainActivity.this, id)
```
这样降低了对接的认知难度。  
这种也许不是最优解决方案，只是一个规范，约定大于配置。
