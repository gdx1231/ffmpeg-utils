package com.gdxsoft.ffmpegUtils.job;

import com.gdxsoft.ffmpegUtils.VideoScale;
import com.gdxsoft.ffmpegUtils.Watermark;

public interface ITaskInfo {

	/**
	 * 当转换开始
	 * 
	 * @param work
	 * @param args
	 */
	void onStart(JobWorker work, Object[] args);

	/**
	 * 当失败时
	 * 
	 * @param work
	 * @param err
	 * @param args
	 */
	void onError(JobWorker work, Exception err, Object[] args);

	/**
	 * 当完成时
	 * 
	 * @param work
	 * @param args
	 */
	void onCompleted(JobWorker work, Object[] args);

	/**
	 * 当执行过程中
	 * 
	 * @param work
	 * @param step
	 * @param args
	 */
	void onStep(JobWorker work, String step, Object[] args);

	/**
	 * 当完成时
	 * 
	 * @param work
	 * @param args
	 */
	void onAlreadyConverted(JobWorker work, Object[] args);
	/**
	 * 检查是否已经处理过
	 * @return
	 */
	boolean checkConverted();
	/**
	 * 来源视频地址
	 * 
	 * @return the sourceVideo
	 */
	String getSourceVideo();

	/**
	 * 来源视频地址
	 * 
	 * @param sourceVideo the sourceVideo to set
	 */
	void setSourceVideo(String sourceVideo);

	/**
	 * 输出视频地址前缀，例如 /home/guolei/video/output/vod_1121<br>
	 * 转换完毕根据前缀添加扩展名，例如：输出文件 mp4为<br>
	 * /home/guolei/video/output/vod_1121.mp4<br>
	 * 输出文件 ts 为 /home/guolei/video/output/vod_1121.ts<br>
	 * 输出文件 m3u8 为 /home/guolei/video/output/vod_1121.m3u8
	 * 
	 * @return the outPrefix
	 */
	String getOutPrefix();

	/**
	 * 输出视频地址前缀，例如 /home/guolei/video/output/vod_1121<br>
	 * 转换完毕根据前缀添加扩展名，例如：输出文件 mp4为<br>
	 * /home/guolei/video/output/vod_1121.mp4<br>
	 * 输出文件 ts 为 /home/guolei/video/output/vod_1121.ts<br>
	 * 输出文件 m3u8 为 /home/guolei/video/output/vod_1121.m3u8
	 * 
	 * @param outPrefix 输出视频地址前缀
	 */
	void setOutPrefix(String outPrefix);

	/**
	 * 输出视频水印
	 * 
	 * @return the waterMark
	 */
	public Watermark getWaterMark();

	/**
	 * 输出视频水印
	 * 
	 * @param waterMark the waterMark to set
	 */
	public void setWaterMark(Watermark waterMark);

	/**
	 * 输出视频码率
	 * 
	 * @return the bitRate
	 */
	public long getBitRate();

	/**
	 * 输出视频码率
	 * 
	 * @param bitRate the bitRate to set
	 */
	public void setBitRate(long bitRate);

	/**
	 * 输出视频分辨率
	 * 
	 * @return the outVideoScale
	 */
	public VideoScale getOutVideoScale();

	/**
	 * 输出视频分辨率
	 * 
	 * @param outVideoScale the outVideoScale to set
	 */
	public void setOutVideoScale(VideoScale outVideoScale);

	/**
	 * 是否转码为 MPEG-TS 格式，默认true
	 * 
	 * @return the covvertToTs
	 */
	public boolean isConvertToTs();

	/**
	 * 是否转码为 MPEG-TS 格式，默认true
	 * 
	 * @param covvertToTs the covvertToTs to set
	 */
	public void setConvertToTs(boolean convertToTs);

	/**
	 * 是否转码为M3u8 格式，默认true
	 * 
	 * @return the convertToM3u8
	 */
	public boolean isConvertToM3u8();

	/**
	 * 是否转码为 M3u8 格式
	 * 
	 * @param convertToM3u8 the convertToM3u8 to set
	 */
	public void setConvertToM3u8(boolean convertToM3u8);
}