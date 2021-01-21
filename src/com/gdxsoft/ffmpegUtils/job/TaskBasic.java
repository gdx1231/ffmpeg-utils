package com.gdxsoft.ffmpegUtils.job;

import com.gdxsoft.ffmpegUtils.VideoScale;
import com.gdxsoft.ffmpegUtils.Watermark;

/**
 * 用于boss和work传递的任务信息
 * 
 * @author admin
 *
 */
public class TaskBasic {

	public TaskBasic() {
	}

	// 源视频地址，例如 /home/guolei/video/source/vod_1121.mov
	private String sourceVideo;
	// 输出视频地址前缀，例如 /home/guolei/video/output/vod_1121
	private String outPrefix;

	// 水印
	private Watermark waterMark;
	// 码率
	private long bitRate;
	// 输出视频分辨率
	private VideoScale outVideoScale;
	// 是否转码为 MPEG-TS 格式，默认true
	private boolean convertToTs = true;
	// 是否转码为 M3u8 格式
	private boolean convertToM3u8 = true;

	/**
	 * 来源视频
	 * 
	 * @return the sourceVideo
	 */
	public String getSourceVideo() {
		return sourceVideo;
	}

	/**
	 * 源视频地址，例如 /home/guolei/video/source/vod_1121.mov
	 * 
	 * @param sourceVideo the sourceVideo to set
	 */
	public void setSourceVideo(String sourceVideo) {
		this.sourceVideo = sourceVideo;
	}

	/**
	 * 输出地址前缀
	 * 
	 * @return the outPrefix
	 */
	public String getOutPrefix() {
		return outPrefix;
	}

	/**
	 * 输出视频地址前缀，例如 /home/guolei/video/output/vod_1121<br>
	 * 转换完毕根据前缀添加扩展名，例如：输出文件 mp4为<br>
	 * /home/guolei/video/output/vod_1121.mp4<br>
	 * 输出文件 ts 为 /home/guolei/video/output/vod_1121.ts<br>
	 * 输出文件 m3u8 为 /home/guolei/video/output/vod_1121.m3u8
	 * 
	 * @param outPrefix 输出视频地址前缀
	 */
	public void setOutPrefix(String outPrefix) {
		this.outPrefix = outPrefix;
	}

	/**
	 * @return the waterMark
	 */
	public Watermark getWaterMark() {
		return waterMark;
	}

	/**
	 * @param waterMark the waterMark to set
	 */
	public void setWaterMark(Watermark waterMark) {
		this.waterMark = waterMark;
	}

	/**
	 * @return the bitRate
	 */
	public long getBitRate() {
		return bitRate;
	}

	/**
	 * @param bitRate the bitRate to set
	 */
	public void setBitRate(long bitRate) {
		this.bitRate = bitRate;
	}

	/**
	 * @return the outVideoScale
	 */
	public VideoScale getOutVideoScale() {
		return outVideoScale;
	}

	/**
	 * @param outVideoScale the outVideoScale to set
	 */
	public void setOutVideoScale(VideoScale outVideoScale) {
		this.outVideoScale = outVideoScale;
	}

	/**
	 * 是否转码为 MPEG-TS 格式，默认true
	 * 
	 * @return the covvertToTs
	 */
	public boolean isConvertToTs() {
		return convertToTs;
	}

	/**
	 * 是否转码为 MPEG-TS 格式，默认true
	 * 
	 * @param convertToTs the covvertToTs to set
	 */
	public void setConvertToTs(boolean convertToTs) {
		this.convertToTs = convertToTs;
	}

	/**
	 * 是否转码为M3u8 格式，默认true
	 * 
	 * @return the convertToM3u8
	 */
	public boolean isConvertToM3u8() {
		return convertToM3u8;
	}

	/**
	 * 是否转码为 M3u8 格式
	 * 
	 * @param convertToM3u8 the convertToM3u8 to set
	 */
	public void setConvertToM3u8(boolean convertToM3u8) {
		this.convertToM3u8 = convertToM3u8;
	}

}
