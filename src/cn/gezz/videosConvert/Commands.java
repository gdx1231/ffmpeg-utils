package cn.gezz.videosConvert;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

public class Commands {

	public static String PATH_FFMPEG = "e:/Guolei/ffmpeg/bin/ffmpeg.exe";
	public static String PATH_FFPROBE = "e:/Guolei/ffmpeg/bin/ffprobe.exe";

	/**
	 * 创建 转码m3u8 视频格式文件
	 * 
	 * @param sourceFile    来源文件
	 * @param m3u8IndexName m3u8索引文件 (含输出目录）
	 * @param segmentPrefix 分片文件前缀 (含输出目录）
	 * @return
	 */
	public static Command createM3u8Command(String sourceFile, String m3u8IndexName, String segmentPrefix) {

		FFmpegBuilder builder3 = new FFmpegBuilder();
		builder3.addInput(sourceFile);
		
		FFmpegOutputBuilder out3 = builder3.addOutput(segmentPrefix + "-%010d.ts");
		out3.setVideoCodec("copy");
		out3.setAudioCodec("copy");
		
		out3.addExtraArgs("-map", "0", "-f", "segment", "-segment_list", m3u8IndexName, "-segment_time", "7");

		Command cmd = new Command();
		cmd.addOutputBuilder(out3);
		cmd.setBuilder(builder3);

		return cmd;
	}

	/**
	 * 转换为ts文件
	 * 
	 * @param sourceFile
	 * @param tsFileName
	 * @return
	 */
	public static Command createCovert2TsCommand(String sourceFile, String tsFileName) {
		FFmpegBuilder builder1 = new FFmpegBuilder();
		builder1.addInput(sourceFile);
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
	 * 
	 * @param sourceFile    原始文件
	 * @param outFile       输出文件
	 * @param codec         编码
	 * @param videoBitRate  视频比特率 ，例如 2 * 1024 * 1024 /2k
	 * @param outVideoScale 输出视频比例，400:-1, 1024:-1 ...
	 * @param waterMark     在视频上添加的水印
	 * @return
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
		// .setFormat("mp4") // Format is inferred from filename, or can be set
		out1.disableSubtitle(); // No subtiles
		out1.setVideoCodec(codec); // Video using x264
		// .setVideoFrameRate(24, 1) // at 24 frames per second
		// .setVideoResolution(480, -1) // at 640x480 resolution
		// .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use
		// experimental specs
		out1.setVideoBitRate(videoBitRate);
		out1.done();

		// FFmpegOutputBuilder out2 = builder.addOutput(outFile1);
		// out2.disableSubtitle(); // No subtiles
		// out2.setVideoCodec("h264_nvenc"); // Video using x264
		// out2.setVideoBitRate(1204 * 1024);
		// out2.done();

		// builder.addExtraArgs("-hwaccel", "cuda");
		// builder.setComplexFilter(
		// "\"[1:v]scale=100:-1[logo];[0:v][logo]overlay=main_w-overlay_w-10:10\"");

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
