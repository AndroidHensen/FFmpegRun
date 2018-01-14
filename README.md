## 实现内容

* 编译FFmpeg成命令行形式
* 测试FFmpeg命令行合并音视频

## 编译FFmpeg成命令行形式

**一、编写本地方法**

```
public class FFmpegRun {

    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeginvoke");
    }

    /**
     * 合并音视频的命令 
     * @param commands
     * @return
     */
    public native static int run(String[] commands);

    /**
     * @param pathAudio
     * @param pathVideo
     * @param pathOutputVideo
     * @return
     */
    public static int mergeVideo(String pathAudio, String pathVideo, String pathOutputVideo) {
        String[] commands = new String[10];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = pathAudio;
        commands[3] = "-i";
        commands[4] = pathVideo;
        commands[5] = "-vcodec";
        commands[6] = "copy";
        commands[7] = "-acodec";
        commands[8] = "copy";
        commands[9] = pathOutputVideo;
        return FFmpegRun.run(commands);
    }
}
```

**二、javah生成文件**

在Terminal中javah我们写好的本地方法
```
C:\Users\handsome\Desktop\FFmpegDouYin\app\src\main\java>javah com.handsome.ffmpegdouyin.FFmpeg.FFmpegRun
```

将生成的.h文件放置jni目录下

**三、移植FFmpeg**

1. 将编译FFmpeg成一个SO库生成的 libffmpeg.so 文件拷贝至 jni 目录
2. 在 jni 目录新建文件 Android.mk Application.mk com_ihubin_ffmpegstudy_FFmpegKit.c
3. 复制FFmpeg源码文件ffmpeg.h, ffmpeg.c, ffmpeg_opt.c, ffmpeg_filter.c，cmdutils.c, cmdutils.h, cmdutils_common_opts.h 到jni目录下

![这里写图片描述](http://img.blog.csdn.net/20180115022756966?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzAzNzk2ODk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**四、修改FFmpeg源码**

**1、ffmpeg.c**

```
//找到mian方法
int main(int argc, char ** argv)
//改名为run方法
int run(int argc, char **argv)
```

**2、ffmpeg.h**

```
//在文件末尾添加函数申明
int run(int argc, char **argv);
```

**3、cmdutils.c**

```
//找到
void exit_program(int ret)
{
    if (program_exit)
        program_exit(ret);

    exit(ret);
}
//改成
int exit_program(int ret)
{
    return ret;
}
```

**4、cmdutils.h**

```
//找到
void exit_program(int ret) av_noreturn;
//改为
int exit_program(int ret);
```

**五、编写c文件**

```
#include "com_handsome_ffmpegdouyin_FFmpeg_FFmpegRun.h"
#include "ffmpeg.h"
#include <string.h>

JNIEXPORT jint JNICALL Java_com_handsome_ffmpegdouyin_FFmpeg_FFmpegRun_run(JNIEnv *env,
        jclass obj, jobjectArray commands) {

    int argc = (*env)->GetArrayLength(env, commands);
    char *argv[argc];

    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        argv[i] = (char*) (*env)->GetStringUTFChars(env, js, 0);
    }
    return run(argc, argv);
}
```

**六、编写Android.mk和Application.mk**

Android.mk

```
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ffmpeg
LOCAL_SRC_FILES := libffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeginvoke
LOCAL_SRC_FILES := com_handsome_ffmpegdouyin_FFmpeg_FFmpegRun.c ffmpeg.c ffmpeg_opt.c cmdutils.c ffmpeg_filter.c

# 这里的地址改成自己的 FFmpeg 源码目录
LOCAL_C_INCLUDES := D:\360Downloads\ffmpeg-3.0.10
LOCAL_LDLIBS := -llog -lz -ldl
LOCAL_SHARED_LIBRARIES := ffmpeg

include $(BUILD_SHARED_LIBRARY)
```

Application.mk

```
APP_ABI := armeabi armeabi-v7a
APP_BUILD_SCRIPT := Android.mk
APP_PLATFORM := android-14
```

**七、ndk-build**

在命令行执行ndk-build，编译会报找不到h文件，这个时候就要从服务器中编译过后的FFmpeg源码中找到对应的h文件添加到自己的本地源码对应的位置即可，具体在三个左右

**八、提取so库**

当你编译成功时，会产生obj的文件夹，我们只需要剪切产生的两个so库放置到libs目录下

![这里写图片描述](http://img.blog.csdn.net/20180115024007820?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzAzNzk2ODk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**九、配置gradle**

```
sourceSets.main {
    jniLibs.srcDirs = ['libs']
    jni.srcDirs = []
}
```

**十、增加权限**

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

## 测试FFmpeg命令行合并音视频

**一、编写代码，执行FFmpeg命令函数**

```
public class MainActivity extends AppCompatActivity {

    public String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar;
    public String VIDEO_PATH = SD_PATH + "kugoumusic/input.mp4";
    public String AUDIO_PATH = SD_PATH + "kugoumusic/input.mp3";
    public String OUTPUT_VIDEO_PATH = SD_PATH + "kugoumusic/output.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mergeVideo(View view) {
        int result = FFmpegRun.mergeVideo(AUDIO_PATH, VIDEO_PATH, OUTPUT_VIDEO_PATH);

        if (result == 0) {
            Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }
}
```

**二、运行代码**

点击按钮合并音视频，这样就大功告成了

[源码下载](https://github.com/AndroidHensen/FFmpegRun)