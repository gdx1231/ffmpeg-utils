package com.gdxsoft.ffmpegUtils;

/**
 * 视频添加的单个水印
 * 
 * @author admin
 *
 */
public class Watermark {

	private Integer top;
	private Integer left;
	private Integer bottom;
	private Integer right;

	private VideoScale logoScale; // 100:-1, 100:25 ...

	private String logoFile;

	private VideoScale videoScale; // 800:-1, 1980:-1 ...

	private String overlay;

	public Watermark() {
	}

	/**
	 * 左上角
	 * 
	 * @param logoFile
	 * @param top
	 * @param left
	 * @param logoScale logo文件缩放比例 100:-1,130:45 ...，可为空
	 */
	public void initLeftTop(String logoFile, int top, int left, VideoScale logoScale) {
		this.logoFile = logoFile;
		this.logoScale = logoScale;

		this.top = top;
		this.left = left;

		this.bottom = null;
		this.right = null;

		this.overlay = "overlay=" + left + ":" + top; // x:y
	}

	/**
	 * 右上角
	 * 
	 * @param logoFile
	 * @param right
	 * @param top
	 * @param logoScale logo文件缩放比例 100:-1,130:45 ...，可为空
	 */
	public void initRightTop(String logoFile, int right, int top, VideoScale logoScale) {
		this.logoFile = logoFile;
		this.logoScale = logoScale;

		this.top = top;
		this.left = null;

		this.bottom = null;
		this.right = right;

		this.overlay = "overlay=main_w-overlay_w-" + right + ":" + top; // x:y
	}

	/**
	 * 左下角
	 * 
	 * @param logoFile
	 * @param left
	 * @param bottom
	 * @param logoScale logo文件缩放比例 100:-1,130:45 ...，可为空
	 */
	public void initLeftBottom(String logoFile, int left, int bottom, VideoScale logoScale) {
		this.logoFile = logoFile;
		this.logoScale = logoScale;
		this.top = null;
		this.left = left;

		this.bottom = bottom;
		this.right = null;

		this.overlay = "overlay=" + left + ":main_h-overlay_h-" + bottom; // x:y
	}

	/**
	 * 右下角
	 * 
	 * @param logoFile
	 * @param bottom
	 * @param right
	 * @param logoScale logo文件缩放比例 100:-1,130:45 ...，可为空
	 */
	public void initRightBottom(String logoFile, int right, int bottom, VideoScale logoScale) {
		this.logoFile = logoFile;
		this.logoScale = logoScale;
		this.top = null;
		this.left = null;

		this.bottom = bottom;
		this.right = right;

		this.overlay = "overlay=main_w-overlay_w-" + right + ":main_h-overlay_h-" + bottom; // x:y
	}

	/**
	 * 输出 filter_complex 命令
	 */
	public String toString() {

		// //
		// "\"[0:v]scale=480:-1[videoResized];[1:v]scale=100:-1[logo];[videoResized][logo]overlay=main_w-overlay_w-10:10\""

		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		String videoName = "[v:0]";
		// 进行视频缩放
		if (this.videoScale != null) {
			sb.append("[0:v]");
			sb.append(this.videoScale.toString());
			sb.append("[videoScaled];");
			videoName = "[videoScaled]";
		}

		String logoName = "[1:v]";
		// logo缩放
		if (this.logoScale != null) {
			sb.append("[1:v]");
			sb.append(this.logoScale.toString());
			sb.append("[logo];");

			logoName = "[logo]";
		}

		// logo在视频上的位置
		sb.append(videoName);
		sb.append(logoName);
		sb.append(this.overlay);

		sb.append("\"");

		return sb.toString();
	}

	/**
	 * @return the top
	 */
	public Integer getTop() {
		return top;
	}

	/**
	 * @return the left
	 */
	public Integer getLeft() {
		return left;
	}

	/**
	 * @return the bottom
	 */
	public Integer getBottom() {
		return bottom;
	}

	/**
	 * @return the right
	 */
	public Integer getRight() {
		return right;
	}

	/**
	 * @return the logoFile
	 */
	public String getLogoFile() {
		return logoFile;
	}

	/**
	 * @param logoFile the logoFile to set
	 */
	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	/**
	 * @return the logoScale
	 */
	public VideoScale getLogoScale() {
		return logoScale;
	}

	/**
	 * @param logoScale the logoScale to set
	 */
	public void setLogoScale(VideoScale logoScale) {
		this.logoScale = logoScale;
	}

	/**
	 * @return the videoScale
	 */
	public VideoScale getVideoScale() {
		return videoScale;
	}

	/**
	 * @param videoScale the videoScale to set
	 */
	public void setVideoScale(VideoScale videoScale) {
		this.videoScale = videoScale;
	}

	/**
	 * logo在视频上的位置
	 * 
	 * @return
	 */
	public String getOverlay() {
		return this.overlay;
	}
}
