package com.gdxsoft.ffmpegUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

public class VideoConvert {
	public final static long BIT_RATE_500K = 500 * 1024L;
	public final static long BIT_RATE_1M = 2 * BIT_RATE_500K;
	public final static long BIT_RATE_2M = 2 * BIT_RATE_1M;
	public final static long BIT_RATE_5M = 5 * BIT_RATE_1M;

	private FFmpeg ffmpeg;
	private FFprobe ffprobe;
	private String codec = "h264";
	private String hwAccel;

	private Watermark watermark;

	public VideoConvert() {
		try {
			ffmpeg = new FFmpeg("e:/Guolei/ffmpeg/bin/ffmpeg.exe");
			ffprobe = new FFprobe("e:/Guolei/ffmpeg/bin/ffprobe.exe");
		} catch (IOException e) {
		}
	}

	public void exec(FFmpegBuilder builder, ProgressListener listener) {
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		FFmpegJob job = executor.createJob(builder, listener);

		job.run();
	}

	public void exec(Command cmd, ProgressListener listener) {
		this.exec(cmd.getBuilder(), listener);
	}

	public void convertTo(String sourceVideo, String outputVideo, long bitRate, VideoScale outVideoScale) {

		Command cmd = Commands.createConvert2Mp4(sourceVideo, outputVideo, codec, bitRate, outVideoScale,
				getWatermark());

		if (this.hwAccel != null && this.hwAccel.trim().length() > 0) {
			cmd.getBuilder().addExtraArgs("-hwaccel", this.hwAccel);
		}

		this.exec(cmd, null);
	}

	public void convert() {

		String sourceFile = "d:/360Rec/a.mp4";
		String outFile = "d:/360Rec/test.mp4";
		String outFile1 = "d:/360Rec/test.ts";
		String outFile2 = "d:/360Rec/test.m3u8";

		VideoScale outVideoScale = new VideoScale(480, -1);

		long t0 = System.currentTimeMillis();

		Watermark logoWateMark = this.getWatermark();

		Command cmd = Commands.createConvert2Mp4(sourceFile, outFile, codec, BIT_RATE_500K, outVideoScale,
				logoWateMark);

		if (this.hwAccel != null && this.hwAccel.trim().length() > 0) {
			cmd.getBuilder().addExtraArgs("-hwaccel", this.hwAccel);
		}
		cmd.getLastOutputBuilder().setVideoCodec(this.codec);
		this.exec(cmd, null);

		// 转成 ts文件
		Command cmdts = Commands.createCovert2TsCommand(outFile, outFile1);
		this.exec(cmdts, null);

		// 分解为 ts 片段文件
		Command cmtM3u8 = Commands.createM3u8Command(outFile1, outFile2, "d:/360Rec/test");
		this.exec(cmtM3u8, null);

		long t1 = System.currentTimeMillis();
		System.out.println("done " + (t1 - t0) / 1000.0);

	}

	/**
	 * @return the hwAccel
	 */
	public String getHwAccel() {
		return hwAccel;
	}

	/**
	 * @param hwAccel the hwAccel to set
	 */
	public void setHwAccel(String hwAccel) {
		this.hwAccel = hwAccel;
	}

	/**
	 * @return the codec
	 */
	public String getCodec() {
		return codec;
	}

	/**
	 * @param codec the codec to set
	 */
	public void setCodec(String codec) {
		this.codec = codec;
	}

	/**
	 * @param watermark the watermark to set
	 */
	public void setWatermark(Watermark watermark) {
		this.watermark = watermark;
	}

	private Watermark getWatermark() {

		return this.watermark;
	}

	public static void main(String[] args) {
		long t0 = System.currentTimeMillis();

		VideoConvert vc = new VideoConvert();
		vc.setHwAccel("cuda");
		vc.setCodec("h264_nvenc");

		String logo = "d:/360Rec/logo.png";
		Watermark wm = new Watermark();
		VideoScale logoSacle = new VideoScale(122, -1);
		wm.initRightTop(logo, 19, 19, logoSacle);
		vc.setWatermark(wm);

		String sourceFile = "d:/360Rec/a.mp4";
		String outFile = "d:/360Rec/test.mp4";

		VideoScale outVideoScale = new VideoScale(480, -1);

		vc.convertTo(sourceFile, outFile, BIT_RATE_1M, outVideoScale);

		// 转成 ts文件
		String outFile1 = "d:/360Rec/test.ts";
		Command cmdts = Commands.createCovert2TsCommand(outFile, outFile1);
		vc.exec(cmdts, null);

		// 分解为 ts 片段文件
		File segmentsPath = new File(outFile1 + "-segments");
		segmentsPath.mkdirs();
		String segmentPrefix = segmentsPath.getPath() + "/segment";
		String m3u8File = segmentsPath.getPath() + "/index.m3u8";
		Command cmtM3u8 = Commands.createM3u8Command(outFile1, m3u8File, segmentPrefix);
		vc.exec(cmtM3u8, null);

		long t1 = System.currentTimeMillis();
		System.out.println("done " + (t1 - t0) / 1000.0);

	}

}
