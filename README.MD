# 通用WebView

- 原生与JS通讯部分，参考 https://github.com/wendux/DSBridge-Android
- 将WebView的可设置属性都开放出来，项目各自定制（由于WebView存在一些安全漏洞，后期考虑针对性的出SafeWebView的Demo）
- 出于实际项目考虑，去除对Android4.2版本以下的支持
- 自定义错误界面（内置）
- 支持加载进度条颜色设置（不显示，可设置为透明）
- 为了兼容Android5.0以下的input标签，文件选择只支持单选

## 引用

1.在根目录build.gradle文件中添加

```gradle
  
 ```

2.项目build.gradle文件中添加

```gradle

 ```

## 使用方法

1.直接使用封装好的WebFragment，内部已经处理好了通用逻辑，如：下载、文件选择等。
可以快速集成：

- 在Activity的onCreate()中添加该WebFragment


```java
    FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            mWebFragment = new WebFragment();
            Bundle bundle = new Bundle();
            bundle.putString(WebFragment.URL, "http://zh.hainan.gov.cn/app/index.html#/flood-situation");
            bundle.putInt(WebFragment.PROGRESSBAR_COLOR, Color.RED);
            mWebFragment.setArguments(bundle);
            ft.add(R.id.flContainer, mWebFragment);
            ft.commit();
```

- 与手机回退按钮联动，复写Activity的onBackPressed()

```java
    @Override
    public void onBackPressed() {
        if (mWebFragment.canGoBack()) {
            mWebFragment.goBack();
            return;
        }
        super.onBackPressed();
    }
```

2.根据项目定制添加自定义交互方式

- 创建新的Fragment，继承WebFragment；
- 复写onViewCreated()：

```java
   mWebViewWrapper.getWebView().addJavascriptObject(new JsApi(), null);
   mWebViewWrapper.getWebView().addJavascriptObject(new JsEchoApi(), "echo");
```

可以参考demo中：AddJSObjectFragment。

3.WebView预加载策略（导致内存泄漏，慎用）

- 创建新的Fragment，继承WebFragment；
- 复写getWebViewWrapper()，传入预加载的WebViewWrapper
- 可以参考demo中MainActivity中preload单击事件、WebFragmentPreloadActivity、PreloadWebFragment。


4.如果有使用input标签进行选择图片

- 先进行初始化图片加载器

```java
            ISNav.getInstance().init(new ImageLoader() {
                @Override
                public void displayImage(Context context, String path, ImageView imageView) {
                    GlideApp.with(context).load(path).into(imageView);
                }
            });
 ```

 5.如果要使用JS和原生进行通讯，web开发者需要自行引入dsbridge.js到自己的web界面中。
 ```html
    <script src="./dsbridge.js"> </script>
  ```

 6.设置当界面onPause()之后，全局设置web页面内JS可以正常执行的（默认不允许）。

  ```java
     WebFragment.setAllowOnPauseExecuteJs(true);
   ```

 7.由于provider标签可以配置文件路径，所以由使用方配置。

 ```xml
        <provider
                android:name="android.support.v4.content.FileProvider"
                android:authorities="${applicationId}.fileprovider"
                android:exported="false"
                android:grantUriPermissions="true">
                <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/filepaths"/>
        </provider>
  ```

## 说明

1、accept表示获取多媒体文件的类型，有：image/* 、video/* 、audio/*
2、capture表示可以捕获系统默认的设备。比如：camera--照相机；camcorder--摄像机；microphone--录音。

- 图片选择或者拍照（为了保证低版本的兼容性，只允许单选）

```html
<div>图片选择</div>
<input type="file" accept="image/*">
```
- 拍照

```html
<div>拍照</div>
<input type="file" accept="image/*" capture="camera">
```

- 视频选择或者视频录制（为了保证低版本的兼容性，只允许单选）
```html
<div>视频选择</div>
<input type="file" accept="video/*">
```

- 视频录制
```html
<div>视频录制</div>
<input type="file" accept="video/*" capture="camcorder">
```

- 音频选择或者音频录制（iOS暂不支持）
```html
<div>音频选择</div>
<input type="file" accept="audio/*">
```

- 录音（iOS暂不支持）
```html
<div>录音</div>
<input type="file" accept="audio/*" capture="microphone">
```
- 组合（根据accept属性让用户选择拍照、视频录制、音频录制，如果不加capture，则选择项会多出手机相册选项）
```html
<div>录音</div>
<input type="file" accept="image/*,video/*,audio/*" capture="camcorder">
```

## 注意事项

- ECloud-Android最低版本支持：5.0，为了保证兼容性，尽量少用css3。
- 截图来源于 https://blog.csdn.net/yu280265067/article/details/73379145
![image.png](http://wiki.istrongcloud.com/Images/3580b8e6/738ee43f/Images/291daf0c-9ba1-4e54-b29d-1bd9f47c7860.png)

## 版本记录

### 1.0.0
- 初始提交

