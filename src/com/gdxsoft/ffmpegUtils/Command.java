package com.gdxsoft.ffmpegUtils;

import java.util.ArrayList;
import java.util.List;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

/**
 * 执行Ffmpeg的命令体
 * 
 * 因为 builder创建的
 * FFmpegOutputBuilder，不可在builder中访问，因此用于保存builder，一个或多个FFmpegOutputBuilder，用于后面访问
 * 
 * @author admin
 *
 */
public class Command {
	// Ffmpeg的命令体
	private FFmpegBuilder builder;
	// 最后一次添加的 输出文件的命令体
	private FFmpegOutputBuilder lastOutputBuilder;
	// 所有输出文件的命令体
	private List<FFmpegOutputBuilder> outputBuilders;

	public Command() {
		outputBuilders = new ArrayList<FFmpegOutputBuilder>();
	}

	/**
	 * 添加输出文件的命令体
	 * 
	 * @param outputBuilder 输出文件的命令体
	 */
	public void addOutputBuilder(FFmpegOutputBuilder outputBuilder) {
		this.lastOutputBuilder = outputBuilder;
		this.outputBuilders.add(outputBuilder);
	}

	/**
	 * 获取 Ffmpeg的命令体
	 * 
	 * @return the builder
	 */
	public FFmpegBuilder getBuilder() {
		return builder;
	}

	/**
	 * 设置 Ffmpeg的命令体
	 * 
	 * @param builder the builder to set
	 */
	public void setBuilder(FFmpegBuilder builder) {
		this.builder = builder;
	}

	/**
	 * 获取最后一次添加的 输出文件的命令体
	 * 
	 * @return the outputBuilder
	 */
	public FFmpegOutputBuilder getLastOutputBuilder() {
		return lastOutputBuilder;
	}

	/**
	 * 获取所有输出文件的命令体
	 * 
	 * @return the outputBuilders
	 */
	public List<FFmpegOutputBuilder> getOutputBuilders() {
		return outputBuilders;
	}

}
