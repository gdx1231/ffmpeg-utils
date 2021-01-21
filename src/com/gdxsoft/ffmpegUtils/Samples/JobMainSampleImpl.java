package com.gdxsoft.ffmpegUtils.Samples;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdxsoft.ffmpegUtils.VideoConvert;
import com.gdxsoft.ffmpegUtils.VideoScale;
import com.gdxsoft.ffmpegUtils.Watermark;
import com.gdxsoft.ffmpegUtils.job.IJobMain;
import com.gdxsoft.ffmpegUtils.job.ITaskInfo;
import com.gdxsoft.ffmpegUtils.job.JobWorker;

/**
 * 一个简单的任务分配的主程序boss<br>
 * 1 扫描视频所在源目录下的所有视频加载到任务队列中<br>
 * 2 启动多线程的worker工作队列<br>
 * 3 worker 通过 主程序boss的 getTask 获取任务并执行<br>
 * 
 * @author admin
 *
 */
public class JobMainSampleImpl implements IJobMain {
	static final Logger LOG = LoggerFactory.getLogger(JobMainSampleImpl.class);

	private List<ITaskInfo> taskList; // 任务队列
	private List<JobWorker> queues;
	private List<Future<String>> futures;
	private ThreadPoolExecutor executor;

	private String sourcePath;
	private String outputPath;
	private String logoPath;

	public JobMainSampleImpl() {
		LOG.info("I'm boss");
	}

	/**
	 * 获取一个work使用的任务信息
	 * 
	 * @return 任务信息
	 */
	public synchronized ITaskInfo getTask() {
		if (taskList == null) { // 没有初始化数据了
			return null;
		}
		if (taskList.size() > 0) {
			ITaskInfo item = taskList.get(0);
			taskList.remove(0);
			return item;
		}

		this.queryMoreTasks();
		if (taskList.size() > 0) {
			ITaskInfo item = taskList.get(0);
			taskList.remove(0);
			return item;
		}
		return null;
	}

	/**
	 * 检查是否所有工作已经完成
	 * 
	 * @return 如果完成返回true,否则false
	 */
	public boolean checkAllDown() {
		return false;
	}

	/**
	 * 获取更多的任务
	 */
	public void queryMoreTasks() {
		if (taskList != null) {
			// 没有更多的了
			return;
		}

		taskList = new ArrayList<ITaskInfo>();
		File sourceDir = new File(sourcePath);
		File outDir = new File(this.outputPath);
		File[] files = sourceDir.listFiles();

		long bitRateOf720p = VideoConvert.BIT_RATE_2M;
		VideoScale p720 = VideoScale.get720P();
		Watermark waterMark = this.createWatermark();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			this.addTask(file, outDir, p720, bitRateOf720p, waterMark);
		}
	}

	/**
	 * 添加到任务队列里
	 * 
	 * @param file       文件
	 * @param outDir     输出目录
	 * @param videoScale 分辨率
	 * @param bitRate    码率
	 * @param waterMark  水印
	 */
	private void addTask(File file, File outDir, VideoScale videoScale, long bitRate, Watermark waterMark) {
		if (file.isDirectory() || file.length() == 0) {
			return;
		}
		String name = file.getName().toUpperCase();
		if (!(name.endsWith(".MP4") || name.endsWith(".MOV") || name.endsWith(".MTK") || name.endsWith(".AVI")
				|| name.endsWith(".WVM"))) {
			return;
		}

		TaskSampleImpl task = new TaskSampleImpl();
		task.setSourceVideo(file.getAbsolutePath());
		task.setBitRate(bitRate);
		task.setWaterMark(waterMark);
		task.setOutVideoScale(videoScale);

		// 输出视频文件目录的前缀
		String outPrefix = outDir.getAbsolutePath() + File.separator + file.getName() + "-output" + File.separator
				+ "720P";
		task.setOutPrefix(outPrefix);

		taskList.add(task);
	}

	public Watermark createWatermark() {
		// 水印
		String logo = this.logoPath;
		if (logo == null) {
			return null;
		}
		Watermark wm = new Watermark();
		// 水印 尺寸，宽度，高度根据图片自动计算
		VideoScale logoSacle = new VideoScale(122, -1);
		// 右上角，头部和右边偏离19像素
		wm.initRightTop(logo, 19, 19, logoSacle);

		return wm;
	}

	/**
	 * 根据 queueLength 初始化worker队列
	 * 
	 * @param queueLength 队列长度
	 */
	public void initQueue(int queueLength) {
		queues = new LinkedList<>();
		futures = new LinkedList<>();
		executor = new ThreadPoolExecutor(queueLength, queueLength, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1));
		for (int i = 0; i < queueLength; i++) {
			String queueName = "WK_" + i;
			JobWorker worker = new JobWorker(queueName, this);
			// cv.setVodCovert(otherDefinedVideoConvert);
			worker.initDefaultVc();

			Future<String> a = executor.submit(worker);// 1
			queues.add(worker);
			futures.add(a);
		}
	}

	public static void main(String[] args) {
		JobMainSampleImpl boss = new JobMainSampleImpl();
		boss.setLogoPath("d:/test/logo.png"); // logo的路径
		boss.setSourcePath("d:/test/source"); // 源视频所在目录
		boss.setOutputPath("d:/test/output"); // 视频输出目录

		boss.queryMoreTasks();

		// 根据计算机和显卡的能力，创建2个队列
		boss.initQueue(2);

		// windows 破解 nvdia 转码并发超过两个， OpenEncodeSessionEx failed: out of memory (10)
		// http://www.smartplatform.top/index.php/archives/84/
		// 特征码:84 C0 74 08 C6 43 38 01 33 C0
		// 修改为:84 C0 90 90 C6 43 38 01 33 C0
		// sed
		// 's/\x84\xC0\x74\x08\xC6\x43\x38\x01\x33\xC0/\x84\xC0\x90\x90\xC6\x43\x38\x01\x33\xC0/g'
		// nvcuvid.dll > nvcuvid-1.dll

		boss.executor.shutdown();
	}

	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @param sourcePath the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * @return the outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @param outputPath the outputPath to set
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	/**
	 * @return the logoPath
	 */
	public String getLogoPath() {
		return logoPath;
	}

	/**
	 * @param logoPath the logoPath to set
	 */
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

}
