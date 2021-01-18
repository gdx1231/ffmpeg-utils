package com.gdxsoft.ffmpegUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
		try {
			ffmpeg = new FFmpeg(pathFfmpeg);
			ffprobe = new FFprobe(pathFfprobe);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	public void exec(FFmpegBuilder builder, ProgressListener listener) {
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		FFmpegJob job = executor.createJob(builder, listener);

		job.run();
	}

	public void exec(Command cmd, ProgressListener listener) {
		this.exec(cmd.getBuilder(), listener);
	}

	/**
	 * 转换视频
	 * 
	 * @param sourceVideoFile
	 * @param outputVideoFile
	 * @param bitRate
	 * @param outVideoScale
	 */
	public void convertTo(File sourceVideoFile, File outputVideoFile, long bitRate, VideoScale outVideoScale) {
		this.convertTo(sourceVideoFile.getAbsolutePath(), outputVideoFile.getAbsolutePath(), bitRate, outVideoScale);
	}

	/**
	 * 转换视频
	 * 
	 * @param sourceVideo
	 * @param outputVideo
	 * @param bitRate
	 * @param outVideoScale
	 */
	public void convertTo(String sourceVideo, String outputVideo, long bitRate, VideoScale outVideoScale) {
		String videoCodec = this.videoDecoder;

		if (videoCodec == null || videoCodec.trim().length() == 0) {
			videoCodec = "libx264";
		}

		Command cmd = Commands.createConvert2Mp4(sourceVideo, outputVideo, this.videoEncoder, bitRate, outVideoScale,
				getWatermark());

		if (this.hwAccel != null && this.hwAccel.trim().length() > 0) {
			cmd.getBuilder().addExtraArgs("-hwaccel", this.hwAccel);
		}

		this.exec(cmd, null);
	}

	/**
	 * 获取视频基础信息
	 * 
	 * @param sourceVideo
	 * @return
	 */
	public VideoInfo queryVodInfo(String sourceVideo) {
		VideoInfo vi = new VideoInfo();
		FFmpegProbeResult probeResult;
		try {

			probeResult = ffprobe.probe(sourceVideo);
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
			vi.setVideoPath(sourceVideo);
			return vi;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	/**
	 * @return the hwAccel
	 */
	public String getHwAccel() {
		return hwAccel;
	}

	/**
	 * @param hwAccel the hwAccel to set
	 */
	public void setHwAccel(String hwAccel) {
		this.hwAccel = hwAccel;
	}

	/**
	 * @return the codec
	 */
	public String getVideoEncoder() {
		return videoEncoder;
	}

	/**
	 * @param codec the codec to set
	 */
	public void setVideoEncodeCodec(String videoEncoder) {
		this.videoEncoder = videoEncoder;
	}

	/**
	 * @param watermark the watermark to set
	 */
	public void setWatermark(Watermark watermark) {
		this.watermark = watermark;
	}

	private Watermark getWatermark() {

		return this.watermark;
	}

	/**
	 * 
	 * @return the videoDecoder
	 */
	public String getVideoDecoder() {
		return videoDecoder;
	}

	/**
	 * @param videoDecoder the videoDecoder to set
	 */
	public void setVideoDecoder(String videoDecoder) {
		this.videoDecoder = videoDecoder;
	}

}
