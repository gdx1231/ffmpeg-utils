package com.gdxsoft.ffmpegUtils;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

/**
 * 创建命令体的静态类，同时提供在 ffmpeg-utils.properties定义的系统参数
 * 
 * @author admin
 *
 */
public class Commands {

	/**
	 * ffmpeg 执行文件目录和文件名 ，例如 c:/ffmpeg/bin/ffmpeg.exe
	 */
	public static String PATH_FFMPEG;
	/**
	 * ffprobe 执行文件目录和文件名 ，例如 c:/ffmpeg/bin/ffprobe.exe
	 */
	public static String PATH_FFPROBE;
	/**
	 * 启用的硬件加速方式，例如 cuvid
	 */
	public static String HWACCEL;
	/**
	 * 启用的硬件加速的h264编码方式，例如 h264_nvenc
	 */
	public static String HW_H264_ENC;

	static final Logger LOG = LoggerFactory.getLogger(Commands.class);
	static {
		String propName = "ffmpeg-utils.properties";
		java.util.Properties props = new java.util.Properties();
		InputStream in = null;
		try {
			in = Commands.class.getClassLoader().getResourceAsStream(propName);
			props.load(in);
			if (props.getProperty("PATH_FFMPEG") != null) {
				PATH_FFMPEG = props.getProperty("PATH_FFMPEG").trim();
			}
			if (props.getProperty("PATH_FFPROBE") != null) {
				PATH_FFPROBE = props.getProperty("PATH_FFPROBE").trim();
			}
			if (props.getProperty("HWACCEL") != null) {
				HWACCEL = props.getProperty("HWACCEL").trim();
			}
			if (props.getProperty("HW_H264_ENC") != null) {
				HW_H264_ENC = props.getProperty("HW_H264_ENC").trim();
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}
		}

	}

	/**
	 * 创建 获取视频的一张封面图（关键帧）命令
	 * 
	 * @param sourceFile     来源视频文件
	 * @param coverImagePath 创建图片文件
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createGetCoverCommand(File sourceFile, File coverImagePath) {
		return createGetCoverCommand(sourceFile.getAbsolutePath(), coverImagePath.getAbsolutePath());
	}

	/**
	 * 创建 获取视频的一张封面图（关键帧）命令
	 * 
	 * @param sourceFile     来源视频文件路径
	 * @param coverImagePath 创建图片路径
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createGetCoverCommand(String sourceFile, String coverImagePath) {
		// ffmpeg -i test.mp4 -vf "select=eq(pict_type\,I)" -frames:v 1 -pix_fmt
		// yuvj422p -vsync vfr -qscale:v 2 -f image2 4.jpg -y
		FFmpegBuilder builder3 = new FFmpegBuilder();
		builder3.addInput(sourceFile);

		FFmpegOutputBuilder out3 = builder3.addOutput(coverImagePath);
		out3.setFormat("image2");
		// 抽取关键帧
		out3.setVideoFilter("\"select=eq(pict_type\\,I)\"");
		// 一张图
		out3.setFrames(1);
		out3.setVideoPixelFormat("yuvj422p");

		// -vsync parameter
		// 0, passthrough 每一帧从解码器到编码器，时间戳保持不变
		// 1, cfr 如果指定了输出帧率，输入帧会按照需要进行复制（如果输出帧率大于输入帧率）或丢弃（如果输出帧率小于输入帧率）
		// 2, vfr 输入帧从解码器到编码器，时间戳保持不变；如果出现相同时间戳的帧，则丢弃之
		// drop 同 passthrough，但将所有帧的时间戳清空
		// -1, auto Chooses between 1 and 2 depending on muxer capabilities. This is the
		// default method.

		// Variable Bit Rate with -qscale

		// You can select a video quality level with -qscale:v n (or the alias -q:v n),
		// where n is a number from 1-31, with 1 being highest quality/largest filesize
		// and 31 being the lowest quality/smallest filesize. This is a variable bit
		// rate mode, roughly analogous to using -qp (constant QP [quantization
		// parameter]) with x264. Most of the time this should be the preferred method.

		// You can select an audio quality level with -qscale:a (or the alias -q:a). The
		// value changes depending on the audio encoder. Since this guide uses
		// libmp3lame see the MP3 Encoding Guide for examples and more information.
		out3.addExtraArgs("-vsync", "vfr", "-qscale:v", "2");

		Command cmd = new Command();
		cmd.addOutputBuilder(out3);
		cmd.setBuilder(builder3);

		return cmd;
	}

	/**
	 * 创建 转码m3u8 视频格式文件，按照每5秒分割一个
	 * 
	 * @param sourceFile    来源文件
	 * @param m3u8IndexName m3u8索引文件 (含输出目录）
	 * @param segmentPrefix 分片文件前缀 (含输出目录）
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createM3u8Command(String sourceFile, String m3u8IndexName, String segmentPrefix) {
		return createM3u8Command(sourceFile, m3u8IndexName, segmentPrefix, 5);
	}

	/**
	 * 创建 转码m3u8 视频格式文件
	 * 
	 * @param sourceFile    来源文件
	 * @param m3u8IndexName m3u8索引文件 (含输出目录）
	 * @param segmentPrefix 分片文件前缀 (含输出目录）
	 * @param segmentTime   分割的时间（秒）
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createM3u8Command(String sourceFile, String m3u8IndexName, String segmentPrefix,
			int segmentTime) {

		FFmpegBuilder builder3 = new FFmpegBuilder();
		builder3.addInput(sourceFile);

		FFmpegOutputBuilder out3 = builder3.addOutput(segmentPrefix + "-%010d.ts");
		out3.setVideoCodec("copy");
		out3.setAudioCodec("copy");

		out3.addExtraArgs("-map", "0", "-f", "segment", "-segment_list", m3u8IndexName, "-segment_time",
				String.valueOf(segmentTime));

		Command cmd = new Command();
		cmd.addOutputBuilder(out3);
		cmd.setBuilder(builder3);

		return cmd;
	}

	/**
	 * 转换为ts文件
	 * 
	 * @param sourceFile 视频源文件
	 * @param tsFile     输出ts视频文件
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createCovert2TsCommand(File sourceFile, File tsFile) {
		return createCovert2TsCommand(sourceFile.getAbsolutePath(), tsFile.getAbsolutePath());
	}

	/**
	 * 转换为ts文件
	 * 
	 * @param sourceFileName 视频源，文件目录及文件名
	 * @param tsFileName     输出ts视频，文件目录及文件名
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createCovert2TsCommand(String sourceFileName, String tsFileName) {
		FFmpegBuilder builder1 = new FFmpegBuilder();
		builder1.addInput(sourceFileName);
		FFmpegOutputBuilder out2 = builder1.addOutput(tsFileName);

		out2.setVideoCodec("copy");
		out2.addExtraArgs("-bsf:v", "h264_mp4toannexb");
		out2.setAudioCodec("copy");

		Command cmd = new Command();
		cmd.addOutputBuilder(out2);
		cmd.setBuilder(builder1);

		return cmd;
	}

	/**
	 * 转换成mp4文件
	 * 
	 * @param sourceFile    原始文件
	 * @param outFile       输出文件
	 * @param codec         编码
	 * @param videoBitRate  视频比特率 ，例如 2 * 1024 * 1024 /2k
	 * @param outVideoScale 输出视频比例，400:-1, 1024:-1 ...
	 * @param waterMark     在视频上添加的水印
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createConvert2Mp4(File sourceFile, File outFile, String codec, long videoBitRate,
			VideoScale outVideoScale, Watermark waterMark) {
		return createConvert2Mp4(sourceFile.getAbsolutePath(), outFile.getAbsolutePath(), codec, videoBitRate,
				outVideoScale, waterMark);
	}

	/**
	 * 转换成mp4文件
	 * 
	 * @param sourceFile    原始文件
	 * @param outFile       输出文件
	 * @param codec         编码
	 * @param videoBitRate  视频比特率 ，例如 2 * 1024 * 1024 /2k
	 * @param outVideoScale 输出视频比例，400:-1, 1024:-1 ...
	 * @param waterMark     在视频上添加的水印
	 * @return 执行Ffmpeg的命令体
	 */
	public static Command createConvert2Mp4(String sourceFile, String outFile, String codec, long videoBitRate,
			VideoScale outVideoScale, Watermark waterMark) {
		FFmpegBuilder builder = new FFmpegBuilder();
		builder.setInput(sourceFile); // Filename, or a FFmpegProbeResult

		if (waterMark != null) {
			builder.addInput(waterMark.getLogoFile());
		}
		builder.overrideOutputFiles(true); // Override the output if it exists

		FFmpegOutputBuilder out1 = builder.addOutput(outFile);
		// Filename for the destination
		out1.setFormat("mp4"); // Format is inferred from filename, or can be set
		out1.disableSubtitle(); // No subtiles
		out1.setVideoCodec(codec); // Video using x264
		// .setVideoFrameRate(24, 1) // at 24 frames per second
		// .setVideoResolution(480, -1) // at 640x480 resolution
		// .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use
		// experimental specs
		out1.setVideoBitRate(videoBitRate);

		// 避免错误 Too many packets buffered for output stream 0:1
		out1.addExtraArgs("-max_muxing_queue_size", "9999");

		out1.done();

		if (waterMark != null) {
			waterMark.setVideoScale(outVideoScale);
			builder.setComplexFilter(waterMark.toString());
		} else {
			// -vf sacle=400:-1
			out1.setVideoFilter(outVideoScale.toString());
		}

		Command cmd = new Command();
		cmd.addOutputBuilder(out1);
		cmd.setBuilder(builder);

		return cmd;
	}

}
