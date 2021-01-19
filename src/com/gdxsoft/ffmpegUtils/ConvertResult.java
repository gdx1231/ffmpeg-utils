package com.gdxsoft.ffmpegUtils;

import java.util.List;

public class ConvertResult {
	private long startTime;
	private long endTime;
	private List<String> args;
	private VideoInfo sourceInfo;
	private VideoInfo outputInfo;

	public ConvertResult() {
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * 执行时间
	 * 
	 * @return
	 */
	public long execTimeMilliSeconds() {
		return this.endTime - this.startTime;
	}

	/**
	 * 来源视频信息
	 * 
	 * @return the sourceInfo
	 */
	public VideoInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * 来源视频信息
	 * 
	 * @param sourceInfo the sourceInfo to set
	 */
	public void setSourceInfo(VideoInfo sourceInfo) {
		this.sourceInfo = sourceInfo;
	}

	/**
	 * 输出视频信息
	 * 
	 * @return the outputInfo
	 */
	public VideoInfo getOutputInfo() {
		return outputInfo;
	}

	/**
	 * 输出视频信息
	 * 
	 * @param outputInfo the outputInfo to set
	 */
	public void setOutputInfo(VideoInfo outputInfo) {
		this.outputInfo = outputInfo;
	}

	/**
	 * ffmpeg 执行参数
	 * 
	 * @return the args
	 */
	public List<String> getArgs() {
		return args;
	}

	/**
	 * ffmpeg 执行参数
	 * 
	 * @param args the args to set
	 */
	public void setArgs(List<String> args) {
		this.args = args;
	}

	/**
	 * 执行开始时间（毫秒）
	 * 
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * 执行开始时间（毫秒）
	 * 
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * 执行结束时间（毫秒）
	 * 
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * 执行结束时间（毫秒）
	 * 
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

}
