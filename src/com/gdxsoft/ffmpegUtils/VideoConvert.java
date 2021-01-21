package com.gdxsoft.ffmpegUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.probe.FFmpegStream.CodecType;
import net.bramp.ffmpeg.progress.ProgressListener;

/**
 * 视频转换类
 * 
 * @author admin
 *
 */
public class VideoConvert {
	static final Logger LOG = LoggerFactory.getLogger(VideoConvert.class);

	public final static long BIT_RATE_500K = 500 * 1024L;
	public final static long BIT_RATE_1M = 2 * BIT_RATE_500K;
	public final static long BIT_RATE_2M = 2 * BIT_RATE_1M;
	public final static long BIT_RATE_5M = 5 * BIT_RATE_1M;

	private FFmpeg ffmpeg;
	private FFprobe ffprobe;
	private String videoEncoder;
	private String videoDecoder;
	private String hwAccel;

	private Watermark watermark;

	private Map<String, VideoInfo> infos;

	/**
	 * 初始化类<br>
	 * 通过 ffmpeg-utils.properties的PATH_FFMPEG和PATH_FFPROBE 定义， 创建 ffmpeg 和 ffprobe对象
	 */
	public VideoConvert() {
		this(Commands.PATH_FFMPEG, Commands.PATH_FFPROBE);
	}

	/**
	 * 初始化类
	 * 
	 * @param pathFfmpeg  ffmpeg 执行文件目录和文件名 ，例如 c:/ffmpeg/bin/ffmpeg.exe
	 * @param pathFfprobe ffprobe 执行文件目录和文件名 ，例如 c:/ffmpeg/bin/ffprobe.exe
	 */
	public VideoConvert(String pathFfmpeg, String pathFfprobe) {
		infos = new HashMap<String, VideoInfo>();
		try {
			ffmpeg = new FFmpeg(pathFfmpeg);
			ffprobe = new FFprobe(pathFfprobe);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * 执行 Ffmpeg命令
	 * 
	 * @param builder
	 * @param listener
	 */
	public void exec(FFmpegBuilder builder, ProgressListener listener) {
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		FFmpegJob job = executor.createJob(builder, listener);

		job.run();
	}

	/**
	 * 执行 Ffmpeg命令
	 * 
	 * @param cmd      执行命令体
	 * @param listener 监听
	 */
	public void exec(Command cmd, ProgressListener listener) {
		this.exec(cmd.getBuilder(), listener);
	}

	/**
	 * 提取一张视频的封面（关键帧）
	 * 
	 * @param sourceVideo 来源视频
	 * @param coverImage  创建的图片
	 * @return 提取结果
	 */
	public ConvertResult converToCoverImage(File sourceVideo, File coverImage) {
		Command cmdts = Commands.createGetCoverCommand(sourceVideo, coverImage);

		ConvertResult rst = new ConvertResult();
		List<String> args = cmdts.getBuilder().build();
		rst.setArgs(args);

		this.exec(cmdts, null);

		rst.setEndTime(System.currentTimeMillis());

		VideoInfo sourceVi = this.queryVodInfo(sourceVideo);
		rst.setSourceInfo(sourceVi);

		VideoInfo coverInfo = new VideoInfo();
		coverInfo.setVideoPath(coverImage.getAbsolutePath());
		rst.setOutputInfo(coverInfo);

		return rst;

	}

	/**
	 * 提取一张视频的封面（关键帧）
	 * 
	 * @param sourceVideo 来源视频
	 * @param coverImage  创建的图片
	 * @return 提取结果
	 */
	public ConvertResult converToCoverImage(String sourceVideo, String coverImage) {
		return this.converToCoverImage(new File(sourceVideo), new File(coverImage));
	}

	/**
	 * 转换视频
	 * 
	 * @param sourceVideoFile 源视频文件
	 * @param outputVideoFile 输出视频文件
	 * @param bitRate         码率
	 * @param outVideoScale   输出视频分辨率
	 */
	public ConvertResult convertTo(File sourceVideoFile, File outputVideoFile, long bitRate, VideoScale outVideoScale) {
		// 创建输出视频所在目录
		outputVideoFile.getParentFile().mkdirs();

		String encoder = this.videoEncoder;

		if (encoder == null || encoder.trim().length() == 0) {
			encoder = "libx264rgb";
		}

		Command cmd = Commands.createConvert2Mp4(sourceVideoFile, outputVideoFile, encoder, bitRate, outVideoScale,
				getWatermark());

		if (this.hwAccel != null && this.hwAccel.trim().length() > 0) {
			cmd.getBuilder().addExtraArgs("-hwaccel", this.hwAccel);
		}

		ConvertResult rst = new ConvertResult();
		List<String> args = cmd.getBuilder().build();
		rst.setArgs(args);

		this.exec(cmd, null);

		rst.setEndTime(System.currentTimeMillis());

		VideoInfo sourceInfo = this.queryVodInfo(sourceVideoFile);
		rst.setSourceInfo(sourceInfo);

		VideoInfo outputInfo = this.queryVodInfo(outputVideoFile);
		rst.setOutputInfo(outputInfo);

		return rst;

	}

	/**
	 * 转换视频
	 * 
	 * @param sourceVideo   源视频文件地址
	 * @param outputVideo   输出视频文件地址
	 * @param bitRate       码率
	 * @param outVideoScale 输出视频分辨率
	 */
	public ConvertResult convertTo(String sourceVideo, String outputVideo, long bitRate, VideoScale outVideoScale) {
		File of = new File(outputVideo);
		File sf = new File(sourceVideo);
		return this.convertTo(sf, of, bitRate, outVideoScale);
	}

	/**
	 * 转成 ts文件
	 * 
	 * @param sourceVideo 源视频文件地址
	 * @param outTs       输出视频文件地址(ts)
	 * @return 转换结果
	 */
	public ConvertResult convertToTs(String sourceVideo, String outTs) {
		return this.convertToTs(new File(sourceVideo), new File(outTs));
	}

	/**
	 * 转成 ts文件
	 * 
	 * @param sourceVideo 源视频文件
	 * @param outTs       输出视频文件
	 * @return 转换结果
	 */
	public ConvertResult convertToTs(File sourceVideo, File outTs) {
		Command cmdts = Commands.createCovert2TsCommand(sourceVideo, outTs);

		ConvertResult rst = new ConvertResult();
		List<String> args = cmdts.getBuilder().build();
		rst.setArgs(args);

		this.exec(cmdts, null);

		rst.setEndTime(System.currentTimeMillis());

		VideoInfo sourceVi = this.queryVodInfo(sourceVideo);
		VideoInfo outputVi = this.queryVodInfo(outTs);

		rst.setOutputInfo(outputVi);
		rst.setSourceInfo(sourceVi);

		return rst;
	}

	/**
	 * 分解为 ts 片段文件
	 * 
	 * @param sourceTsVideo 源文件（.ts）
	 * @param outFilePrefix 分片文件前缀 例如
	 *                      /videos/vod_1010，会创建为/videos/vod_1010.m3u8索引文件和
	 *                      /videos/vod_1010-0000000000.ts，/videos/vod_1010-0000000001.ts等分片文件
	 * @param segmentTime   分片时间
	 * @return 转换结果
	 */
	public ConvertResult convertToM3u8(File sourceTsVideo, String outFilePrefix, int segmentTime) {

		// 分解为 ts 片段文件
		File outM3u8 = new File(outFilePrefix + ".m3u8");
		String segmentPrefix = outFilePrefix; // 分片文件前缀
		Command cmtM3u8 = Commands.createM3u8Command(sourceTsVideo.getAbsolutePath(), outM3u8.getAbsolutePath(),
				segmentPrefix, segmentTime);

		ConvertResult rst = new ConvertResult();

		List<String> args = cmtM3u8.getBuilder().build();
		rst.setArgs(args);
		this.exec(cmtM3u8, null);

		rst.setEndTime(System.currentTimeMillis());

		VideoInfo tsInfo = this.queryVodInfo(sourceTsVideo);
		rst.setSourceInfo(tsInfo);

		// 分片文件信息
		VideoInfo m3u8Info = this.queryM3u8Info(outM3u8);

		rst.setOutputInfo(m3u8Info);

		return rst;
	}

	/**
	 * 根据 M3u8 索引文件，获取分片文件列表
	 * 
	 * @param m3u8IndexFile 分片文件索引文件地址，例如 /videos/vod_1011.mu38
	 * @return 视频信息
	 */
	public VideoInfo queryM3u8Info(String m3u8IndexFile) {
		return this.queryM3u8Info(new File(m3u8IndexFile));
	}

	/**
	 * 根据 M3u8 索引文件，获取分片文件列表
	 * 
	 * @param m3u8IndexFile 分片文件索引文件，例如 /videos/vod_1011.mu38
	 * @return 视频信息
	 */
	public VideoInfo queryM3u8Info(File m3u8IndexFile) {
		VideoInfo m3u8Info = new VideoInfo();
		m3u8Info.setM3u8File(true);
		m3u8Info.setVideoPath(m3u8IndexFile.getAbsolutePath());

		List<String> lines = null;
		try {
			lines = FileUtils.readLines(m3u8IndexFile, "utf-8");
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
			return null;
		}

		// 根据索引，添加分片文件
		String path = m3u8IndexFile.getParent();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();
			if (line.startsWith("#")) { // 备注
				continue;
			}
			String segmentFilePath = path + File.separator + line.trim();
			m3u8Info.getSegmentList().add(segmentFilePath);
		}

		return m3u8Info;
	}

	/**
	 * 获取视频基础信息
	 * 
	 * @param videoFile 视频文件
	 * @return 视频信息
	 */
	public VideoInfo queryVodInfo(File videoFile) {
		String videoPath = videoFile.getAbsolutePath();

		String viCode = videoPath + "_" + videoFile.length() + "_" + videoFile.lastModified();

		if (this.infos.containsKey(viCode)) {
			// 从缓存中取
			return this.infos.get(viCode);
		}

		VideoInfo vi = new VideoInfo();
		FFmpegProbeResult probeResult;
		try {

			probeResult = ffprobe.probe(videoPath);
			FFmpegFormat format = probeResult.getFormat();

			List<FFmpegStream> streams = probeResult.getStreams();
			for (int i = 0; i < streams.size(); i++) {
				FFmpegStream stream = probeResult.getStreams().get(i);
				if (CodecType.AUDIO == stream.codec_type) {
					vi.setAudioStream(stream); // 音频
				} else if (CodecType.VIDEO == stream.codec_type) {
					vi.setVideoStream(stream); // 视频
				} else {
					LOG.warn("纳尼？");
				}
			}
			vi.setFormat(format);
			vi.setVideoPath(videoPath);

			// 放到缓存中
			this.infos.put(viCode, vi);

			return vi;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}

	}

	/**
	 * 获取视频基础信息
	 * 
	 * @param videoPath 视频的目录和文件名
	 * @return 视频信息
	 */
	public VideoInfo queryVodInfo(String videoPath) {
		return this.queryVodInfo(new File(videoPath));
	}

	/**
	 * 获取硬件加速
	 * 
	 * @return the hwAccel
	 */
	public String getHwAccel() {
		return hwAccel;
	}

	/**
	 * 设置硬件加速
	 * 
	 * @param hwAccel the hwAccel to set
	 */
	public void setHwAccel(String hwAccel) {
		this.hwAccel = hwAccel;
	}

	/**
	 * 获取视频编码
	 * 
	 * @return the videoEncoder
	 */
	public String getVideoEncoder() {
		return videoEncoder;
	}

	/**
	 * 设置视频编码
	 * 
	 * @param videoEncoder the videoEncoder to set
	 */
	public void setVideoEncoder(String videoEncoder) {
		this.videoEncoder = videoEncoder;
	}

	/**
	 * 设置水印
	 * 
	 * @param watermark the watermark to set
	 */
	public void setWatermark(Watermark watermark) {
		this.watermark = watermark;
	}

	/**
	 * 获取水印
	 * 
	 * @return the watermark
	 */
	private Watermark getWatermark() {

		return this.watermark;
	}

	/**
	 * 获取视频解码器
	 * 
	 * @return the videoDecoder
	 */
	public String getVideoDecoder() {
		return videoDecoder;
	}

	/**
	 * 设置视频解码器
	 * 
	 * @param videoDecoder the videoDecoder to set
	 */
	public void setVideoDecoder(String videoDecoder) {
		this.videoDecoder = videoDecoder;
	}

}
