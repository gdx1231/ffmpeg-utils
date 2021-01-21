package com.gdxsoft.ffmpegUtils.Samples;

import java.io.File;

import com.gdxsoft.ffmpegUtils.*;

public class test {
	public static void main(String[] args) {
		
		long t0 = System.currentTimeMillis();

		VideoConvert vc = new VideoConvert();
		// vc.setHwAccel("cuda");
		// vc.setCodec("h264_nvenc");

		vc.setHwAccel(Commands.HWACCEL);
		vc.setVideoEncodeCodec(Commands.HW_H264_ENC);

		String logo = "d:/360Rec/logo.png";
		Watermark wm = new Watermark();
		VideoScale logoSacle = new VideoScale(122, -1);
		wm.initRightTop(logo, 19, 19, logoSacle);
		vc.setWatermark(wm);

		String sourceFile = "d:/360Rec/a.mp4";
		String outFile = "d:/360Rec/test.mp4";

		VideoScale outVideoScale = new VideoScale(480, -1);

		System.out.println("Convert from " + sourceFile + " to " + outFile);
		vc.convertTo(sourceFile, outFile, VideoConvert.BIT_RATE_1M, outVideoScale);
		long t1 = System.currentTimeMillis();
		System.out.println("Converted " + +(t1 - t0) / 1000.0);

		// 转成 ts文件
		String outFile1 = "d:/360Rec/test.ts";
		Command cmdts = Commands.createCovert2TsCommand(outFile, outFile1);
		System.out.println("Convert from " + outFile + " to " + outFile1);
		vc.exec(cmdts, null);
		long t2 = System.currentTimeMillis();
		System.out.println("Converted " + (t2 - t1) / 1000.0);

		// 分解为 ts 片段文件
		File segmentsPath = new File(outFile1 + "-segments");
		segmentsPath.mkdirs();
		String segmentPrefix = segmentsPath.getPath() + "/segment";
		String m3u8File = segmentsPath.getPath() + "/index.m3u8";
		System.out.println("Convert from " + outFile1 + " to " + m3u8File);
		Command cmtM3u8 = Commands.createM3u8Command(outFile1, m3u8File, segmentPrefix);
		vc.exec(cmtM3u8, null);

		long t3 = System.currentTimeMillis();
		System.out.println("Convert done " + (t3 - t2) / 1000.0);

	}
}
