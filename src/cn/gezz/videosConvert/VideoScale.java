package cn.gezz.videosConvert;

/**
 * 视频缩放比例
 * 
 * @author admin
 *
 */
public class VideoScale {
	private int width;
	private int height;

	public VideoScale() {
	}

	/**
	 * 初始化视频缩放比例
	 * @param width
	 * @param height
	 */
	public VideoScale(int width, int height) {
		this.setHeight(height);
		this.setWidth(width);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	public String toString() {
		return "scale=" + this.width + ":" + this.height;
	}

}
