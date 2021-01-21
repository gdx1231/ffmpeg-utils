package com.gdxsoft.ffmpegUtils.job;

import java.io.File;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdxsoft.ffmpegUtils.Commands;
import com.gdxsoft.ffmpegUtils.ConvertResult;
import com.gdxsoft.ffmpegUtils.VideoConvert;
import com.gdxsoft.ffmpegUtils.VideoInfo;
import com.gdxsoft.ffmpegUtils.VideoScale;

/**
 * 视频转换工人
 * 
 * @author admin
 *
 */
public class JobWorker implements Callable<String> {
	static final Logger LOG = LoggerFactory.getLogger(JobWorker.class);
	private String workName;
	private IJobMain jobMain;
	private VideoConvert vodCovert;
	private ITaskInfo taskInfo;

	/**
	 * 初始化工人
	 * 
	 * @param workName 名称
	 * @param jobMain  调用者boss
	 */
	public JobWorker(String workName, IJobMain jobMain) {
		this.workName = workName;
		this.jobMain = jobMain;

	}

	/**
	 * 根据ffmpeg-utils.properties的定义，初始化默认的 VideoConvert
	 * 
	 * @return the VideoConvert
	 */
	public VideoConvert initDefaultVc() {
		VideoConvert vc = new VideoConvert();
		vc.setHwAccel(Commands.HWACCEL);
		vc.setVideoEncoder(Commands.HW_H264_ENC);

		this.vodCovert = vc;
		return vc;
	}

	/**
	 * 当通过 ThreadPoolExecutor加载时执行
	 */
	@Override
	public String call() throws Exception {
		LOG.info(this.workName + " start");
		while (true) {
			ITaskInfo item = jobMain.getTask();
			if (item == null) {
				if (jobMain.checkAllDown()) { // 所有执行完毕
					LOG.info(this.workName + " quit");
					break;
				}
				Thread.sleep(589);
			} else {
				LOG.info(this.workName + " " + item);
				this.convertToMp4TsM3u8(item);
			}
		}
		return this.workName;
	}

	/**
	 * 根据原始视频的长宽比，获取输出视频的分辨<br>
	 * 当source.width > source.height out.height = -1 根据视频内容自动计算高度<br>
	 * 当source.width <= source.height out.width = -1 根据视频内容自动计算高度
	 * 
	 * @return 输出视频的分辨
	 */
	private VideoScale calcOutVideoScale(VideoInfo info) {

		VideoScale outVideoScale = new VideoScale();
		outVideoScale.setHeight(this.taskInfo.getOutVideoScale().getHeight());
		outVideoScale.setWidth(this.taskInfo.getOutVideoScale().getWidth());

		int width = info.getVideoStream().width;
		int height = info.getVideoStream().height;

		LOG.info(width + "x" + height);
		if (width > height) {
			outVideoScale.setHeight(-1); // 根据视频内容自动计算高度
		} else {
			outVideoScale.setWidth(-1); // 根据视频内容自动计算高度
		}
		return outVideoScale;
	}

	/**
	 * 按顺序转换视频为 mp4, ts, m3u8
	 * 
	 * @param taskInfo
	 * @return 工作结果
	 * @throws Exception
	 */
	public JobResult convertToMp4TsM3u8(ITaskInfo taskInfo) throws Exception {
		this.taskInfo = taskInfo;
		JobResult jobResult = new JobResult();
		Object[] args = new Object[3];
		args[0] = jobResult;

		if (taskInfo.checkConverted()) {
			LOG.info("已经转换过了");
			taskInfo.onAlreadyConverted(this, args);
			return jobResult;
		}

		VideoConvert vc = this.getVodCovert();

		vc.setWatermark(taskInfo.getWaterMark());

		// 视频的 duration_ts is in the media's time_base, e.g. 1s = 1/30014
		// durationMilliSeconds = Math.round((1.0 / 30014.0) *
		// vodInfo.getVideoStream().duration_ts * 1000);

		taskInfo.onStart(this, args);

		ConvertResult rstMp4 = null;
		try {
			// 转换为 720P格式文件，最大码率2M
			rstMp4 = this.convertToMp4();
			jobResult.addResult("mp4", rstMp4);
			args[2] = rstMp4;
			taskInfo.onStep(this, "mp4", args);

		} catch (Exception err) {
			taskInfo.onError(this, err, args);
			throw new Exception("转换mp4出错，" + err.getLocalizedMessage());
		}

		File outMp4 = new File(rstMp4.getOutputInfo().getVideoPath());

		try {
			ConvertResult rstCover = this.converToCoverImage(outMp4);
			jobResult.addResult("cover", rstCover);
			args[2] = rstCover;
			taskInfo.onStep(this, "cover", args);
		} catch (Exception err) {
			taskInfo.onError(this, err, args);
			throw new Exception("提取封面出错，" + err.getLocalizedMessage());
		}
		ConvertResult rstTs = null;
		if (this.taskInfo.isConvertToTs()) {
			// 转成 ts文件
			try {
				rstTs = this.convertToTs(outMp4);
				jobResult.addResult("ts", rstTs);

				args[2] = rstTs;
				taskInfo.onStep(this, "ts", args);
			} catch (Exception err) {
				taskInfo.onError(this, err, args);
				throw new Exception("转换ts出错，" + err.getLocalizedMessage());
			}
		}

		ConvertResult rstM3u8 = null;
		File outTs = null;
		if (this.taskInfo.isConvertToTs() && this.taskInfo.isConvertToM3u8()) {
			// 分解为 ts 片段文件
			outTs = new File(rstTs.getOutputInfo().getVideoPath());
			try {
				rstM3u8 = this.convertToM3u8(outTs);
				jobResult.addResult("m3u8", rstM3u8);

				args[2] = rstM3u8;
				taskInfo.onStep(this, "m3u8", args);
			} catch (Exception err) {
				taskInfo.onError(this, err, args);
				throw new Exception("转换 M3u8 出错，" + err.getLocalizedMessage());
			}
		}

		// 删除ts文件
		// outTs.deleteOnExit();

		taskInfo.onCompleted(this, args);
		return jobResult;
	}

	/**
	 * 获取一张视频的封面（关键帧）
	 * 
	 * @param sourceVideo 来源视频
	 * @return 提取结果
	 */
	public ConvertResult converToCoverImage(File sourceVideo) {
		File coverImage = new File(this.taskInfo.getOutPrefix() + ".cover.jpg");
		LOG.info("get cover  from " + sourceVideo + " to " + coverImage);

		ConvertResult result = this.vodCovert.converToCoverImage(sourceVideo, coverImage);
		LOG.info("get cover done " + result.execTimeMilliSeconds() / 1000.0);

		return result;
	}

	/**
	 * 分解为 ts 片段文件
	 * 
	 * @param outTs
	 * @return 转换结果
	 */
	public ConvertResult convertToM3u8(File outTs) {
		VideoConvert vc = this.getVodCovert();

		// 分解为 ts 片段文件
		File outM3u8 = new File(this.taskInfo.getOutPrefix() + ".m3u8");

		LOG.info("Split from " + outTs + " to " + outM3u8);
		ConvertResult rst = vc.convertToM3u8(outTs, this.taskInfo.getOutPrefix(), 5);
		long milliSecondsM3u8 = rst.execTimeMilliSeconds();

		LOG.info("Convert done " + milliSecondsM3u8 / 1000.0);

		return rst;
	}

	/**
	 * 转成 ts文件
	 * 
	 * @param outMp4
	 * @return 转换结果
	 */
	public ConvertResult convertToTs(File outMp4) {
		VideoConvert vc = this.getVodCovert();
		// 转成 ts文件
		File outTs = new File(this.taskInfo.getOutPrefix() + ".ts");
		LOG.info("Convert from " + outMp4 + " to " + outTs);
		ConvertResult rst = vc.convertToTs(outMp4, outTs);
		long milliSecondsTs = rst.execTimeMilliSeconds();
		LOG.info("Converted " + (milliSecondsTs) / 1000.0);

		return rst;
	}

	/**
	 * 转换为MP4
	 * 
	 * @return 转换结果
	 */
	public ConvertResult convertToMp4() {
		VideoConvert vc = this.getVodCovert();
		vc.setWatermark(this.taskInfo.getWaterMark());

		File sourceVideo = new File(taskInfo.getSourceVideo());
		// 原视频摘要信息
		VideoInfo vodInfo = vc.queryVodInfo(sourceVideo.getAbsoluteFile());
		// 获取输出视频 width, height
		VideoScale outVideoScale = this.calcOutVideoScale(vodInfo);

		File outMp4 = new File(this.taskInfo.getOutPrefix() + ".mp4");

		LOG.info("Convert from " + sourceVideo + " to " + outMp4);

		ConvertResult rst = vc.convertTo(sourceVideo, outMp4, this.taskInfo.getBitRate(), outVideoScale);

		long milliSecondsMp4 = rst.execTimeMilliSeconds();
		LOG.info("Converted " + milliSecondsMp4 / 1000.0);

		return rst;
	}

	/**
	 * @return the workName
	 */
	public String getWorkName() {
		return workName;
	}

	/**
	 * @return the jobMain
	 */
	public IJobMain getJobMain() {
		return jobMain;
	}

	/**
	 * @return the vodCovert
	 */
	public VideoConvert getVodCovert() {
		return vodCovert;
	}

	/**
	 * @param vodCovert the vodCovert to set
	 */
	public void setVodCovert(VideoConvert vodCovert) {
		this.vodCovert = vodCovert;
	}

	/**
	 * 获取任务信息
	 * 
	 * @return the taskInfo
	 */
	public ITaskInfo getTaskInfo() {
		return taskInfo;
	}

}
