package com.gdxsoft.ffmpegUtils.Samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdxsoft.ffmpegUtils.job.ITaskInfo;
import com.gdxsoft.ffmpegUtils.job.JobWorker;
import com.gdxsoft.ffmpegUtils.job.TaskBasic;

/**
 * 示例任务对象
 * 
 * @author admin
 *
 */
public class TaskSampleImpl extends TaskBasic implements ITaskInfo {
	static final Logger LOG = LoggerFactory.getLogger(TaskSampleImpl.class);

	/**
	 * 
	 */
	public TaskSampleImpl() {
		super();
	}

	/**
	 * 当完成时
	 * 
	 * @param work
	 * @param args
	 */
	public void onCompleted(JobWorker work, Object[] args) {
		LOG.info("onCompleted");
	}

	/**
	 * 当转换开始
	 * 
	 * @param work
	 * @param args
	 */
	@Override
	public void onStart(JobWorker work, Object[] args) {
		LOG.info("onStart");
	}

	/**
	 * 当失败时
	 * 
	 * @param work
	 * @param err
	 * @param args
	 */
	@Override
	public void onError(JobWorker work, Exception err, Object[] args) {
		LOG.info("onError");
	}

	/**
	 * 当执行过程中
	 * 
	 * @param work
	 * @param step
	 * @param args
	 */
	@Override
	public void onStep(JobWorker work, String step, Object[] args) {
		LOG.info("onStep");
	}
 
	@Override
	public void onAlreadyConverted(JobWorker work, Object[] args) {
		LOG.info("onAlreadyConveted");
	}

	/**
	 * 检查是否已经处理过
	 */
	@Override
	public boolean checkConverted() {
		return false;
	}

}
