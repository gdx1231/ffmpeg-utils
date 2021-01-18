package com.gdxsoft.ffmpegUtils;

import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class VideoInfo {
	private String videoPath;
	private FFmpegFormat format;
	private FFmpegStream VideoStream;
	private FFmpegStream AudioStream;

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
		return VideoStream;
	}

	/**
	 * @param videoStream the videoStream to set
	 */
	public void setVideoStream(FFmpegStream videoStream) {
		VideoStream = videoStream;
	}

	/**
	 * @return the audioStream
	 */
	public FFmpegStream getAudioStream() {
		return AudioStream;
	}

	/**
	 * @param audioStream the audioStream to set
	 */
	public void setAudioStream(FFmpegStream audioStream) {
		AudioStream = audioStream;
	}

}
