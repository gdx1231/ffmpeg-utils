package com.gdxsoft.ffmpegUtils.Samples;


import com.gdxsoft.ffmpegUtils.*;
import com.gdxsoft.ffmpegUtils.job.JobWorker;

public class test {
	public static void main(String[] args) throws Exception {
		String sourceFile = "d:/360Rec/test.mp4";
		String outFilePrefix = "d:/360Rec/test.mp4.output/720P";

		JobWorker worker = new JobWorker("", null);
		worker.initDefaultVc();
		
		//		VideoConvert vc = new VideoConvert();
		//		vc.setHwAccel(Commands.HWACCEL);
		//		vc.setVideoEncodeCodec(Commands.HW_H264_ENC);
		//		worker.setVodCovert(vc);

		String logo = "d:/360Rec/logo.png";
		Watermark wm = new Watermark();
		VideoScale logoSacle = new VideoScale(122, -1);
		wm.initRightTop(logo, 29, 19, logoSacle);

		VideoScale outVideoScale = VideoScale.get720P();

		TaskSampleImpl task = new TaskSampleImpl();
		task.setBitRate(VideoConvert.BIT_RATE_2M);
		
		task.setSourceVideo(sourceFile);
		task.setOutPrefix(outFilePrefix);
		
		task.setOutVideoScale(outVideoScale);
		task.setWaterMark(wm);
		
		
		worker.convertToMp4TsM3u8(task);


	}
 

}
