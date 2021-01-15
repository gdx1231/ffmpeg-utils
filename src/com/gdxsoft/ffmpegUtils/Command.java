package com.gdxsoft.ffmpegUtils;

import java.util.ArrayList;
import java.util.List;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

/**
 * 因为 builder创建的
 * FFmpegOutputBuilder，不可在builder中访问，因此用于保存builder，一个或多个FFmpegOutputBuilder，用于后面访问
 * 
 * @author admin
 *
 */
public class Command {
	private FFmpegBuilder builder;
	private FFmpegOutputBuilder lastOutputBuilder;
	private List<FFmpegOutputBuilder> outputBuilders;

	public Command() {
		outputBuilders = new ArrayList<FFmpegOutputBuilder>();
	}

	public void addOutputBuilder(FFmpegOutputBuilder outputBuilder) {
		this.lastOutputBuilder = outputBuilder;
		this.outputBuilders.add(outputBuilder);
	}

	/**
	 * @return the builder
	 */
	public FFmpegBuilder getBuilder() {
		return builder;
	}

	/**
	 * @param builder the builder to set
	 */
	public void setBuilder(FFmpegBuilder builder) {
		this.builder = builder;
	}

	/**
	 * @return the outputBuilder
	 */
	public FFmpegOutputBuilder getLastOutputBuilder() {
		return lastOutputBuilder;
	}

	/**
	 * @return the outputBuilders
	 */
	public List<FFmpegOutputBuilder> getOutputBuilders() {
		return outputBuilders;
	}

}
