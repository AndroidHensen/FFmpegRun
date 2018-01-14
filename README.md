## ʵ������

* ����FFmpeg����������ʽ
* ����FFmpeg�����кϲ�����Ƶ

## ����FFmpeg����������ʽ

**һ����д���ط���**

```
public class FFmpegRun {

    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeginvoke");
    }

    /**
     * �ϲ�����Ƶ������ 
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

**����javah�����ļ�**

��Terminal��javah����д�õı��ط���
```
C:\Users\handsome\Desktop\FFmpegDouYin\app\src\main\java>javah com.handsome.ffmpegdouyin.FFmpeg.FFmpegRun
```

�����ɵ�.h�ļ�����jniĿ¼��

**������ֲFFmpeg**

1. ������FFmpeg��һ��SO�����ɵ� libffmpeg.so �ļ������� jni Ŀ¼
2. �� jni Ŀ¼�½��ļ� Android.mk Application.mk com_ihubin_ffmpegstudy_FFmpegKit.c
3. ����FFmpegԴ���ļ�ffmpeg.h, ffmpeg.c, ffmpeg_opt.c, ffmpeg_filter.c��cmdutils.c, cmdutils.h, cmdutils_common_opts.h ��jniĿ¼��

![����дͼƬ����](http://img.blog.csdn.net/20180115022756966?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzAzNzk2ODk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**�ġ��޸�FFmpegԴ��**

**1��ffmpeg.c**

```
//�ҵ�mian����
int main(int argc, char ** argv)
//����Ϊrun����
int run(int argc, char **argv)
```

**2��ffmpeg.h**

```
//���ļ�ĩβ��Ӻ�������
int run(int argc, char **argv);
```

**3��cmdutils.c**

```
//�ҵ�
void exit_program(int ret)
{
    if (program_exit)
        program_exit(ret);

    exit(ret);
}
//�ĳ�
int exit_program(int ret)
{
    return ret;
}
```

**4��cmdutils.h**

```
//�ҵ�
void exit_program(int ret) av_noreturn;
//��Ϊ
int exit_program(int ret);
```

**�塢��дc�ļ�**

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

**������дAndroid.mk��Application.mk**

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

# ����ĵ�ַ�ĳ��Լ��� FFmpeg Դ��Ŀ¼
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

**�ߡ�ndk-build**

��������ִ��ndk-build������ᱨ�Ҳ���h�ļ������ʱ���Ҫ�ӷ������б�������FFmpegԴ�����ҵ���Ӧ��h�ļ���ӵ��Լ��ı���Դ���Ӧ��λ�ü��ɣ���������������

**�ˡ���ȡso��**

�������ɹ�ʱ�������obj���ļ��У�����ֻ��Ҫ���в���������so����õ�libsĿ¼��

![����дͼƬ����](http://img.blog.csdn.net/20180115024007820?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzAzNzk2ODk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**�š�����gradle**

```
sourceSets.main {
    jniLibs.srcDirs = ['libs']
    jni.srcDirs = []
}
```

**ʮ������Ȩ��**

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

## ����FFmpeg�����кϲ�����Ƶ

**һ����д���룬ִ��FFmpeg�����**

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

**�������д���**

�����ť�ϲ�����Ƶ�������ʹ󹦸����

[Դ������](https://github.com/AndroidHensen/FFmpegRun)