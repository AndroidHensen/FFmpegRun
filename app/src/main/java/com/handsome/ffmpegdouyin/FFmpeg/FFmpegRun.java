package com.handsome.ffmpegdouyin.FFmpeg;

public class FFmpegRun {

    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeginvoke");
    }

    /**
     * C:\Users\handsome\Desktop\FFmpegDouYin\app\src\main\java>javah com.handsome.ffmpegdouyin.FFmpeg.FFmpegRun
     *
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
