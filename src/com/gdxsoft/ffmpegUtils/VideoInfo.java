package com.gdxsoft.ffmpegUtils;

import java.util.ArrayList;
import java.util.List;

import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegStream;

/**
 * 视频信息
 * @author admin
 *
 */
public class VideoInfo {
	private String videoPath;
	private FFmpegFormat format;
	private FFmpegStream videoStream;
	private FFmpegStream audioStream;
	private boolean m3u8File;
	
	// ts 分片文件清单
	private List<String> segmentList = new ArrayList<String>();

	/**
	 * @return the videoPath
	 */
	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * @param videoPath the videoPath to set
	 */
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	/**
	 * @return the format
	 */
	public FFmpegFormat getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(FFmpegFormat format) {
		this.format = format;
	}

	/**
	 * @return the videoStream
	 */
	public FFmpegStream getVideoStream() {
		return videoStream;
	}

	/**
	 * @param videoStream the videoStream to set
	 */
	public void setVideoStream(FFmpegStream videoStream) {
		this.videoStream = videoStream;
	}

	/**
	 * @return the audioStream
	 */
	public FFmpegStream getAudioStream() {
		return audioStream;
	}

	/**
	 * @param audioStream the audioStream to set
	 */
	public void setAudioStream(FFmpegStream audioStream) {
		this.audioStream = audioStream;
	}

	/**
	 * @return the segmentList
	 */
	public List<String> getSegmentList() {
		return segmentList;
	}

	/**
	 * @param segmentList the segmentList to set
	 */
	public void setSegmentList(List<String> segmentList) {
		this.segmentList = segmentList;
	}

	/**
	 * 是否是 m3u8索引视频
	 * @return the m3u8File
	 */
	public boolean isM3u8File() {
		return m3u8File;
	}

	/**
	 * @param m3u8File the m3u8File to set
	 */
	public void setM3u8File(boolean m3u8File) {
		this.m3u8File = m3u8File;
	}

}
