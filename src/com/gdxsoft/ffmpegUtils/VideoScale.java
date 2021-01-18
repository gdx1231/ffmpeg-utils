package com.gdxsoft.ffmpegUtils;

/**
 * 视频缩放比例
 * 
 * @author admin
 *
 */
public class VideoScale {

	/**
	 * 720 x 480
	 * 
	 * @return
	 */
	public static VideoScale get480P() {
		// 480P通常用在DVD里，额定分辨率为720*480
		// 属于480p标准常见分辨率有640*480、720*480、800*480、854*480。480p通常应用在使用NTSC制式的国家和地区，例如北美、日本、台湾等。
		return new VideoScale(720, 480);
	}

	/**
	 * 1280 x 720
	 * 
	 * @return
	 */
	public static VideoScale get720P() {
		// 720P是美国电影电视工程师协会（SMPTE）制定的高等级高清数字电视的格式标准，有效显示格式为：1280×720。
		return new VideoScale(1280, 720);
	}

	/**
	 * 1920 x 1080
	 * 
	 * @return
	 */
	public static VideoScale get1080P() {
		return new VideoScale(1920, 1080);
	}

	/**
	 * 3840 x 2160
	 * 
	 * @return
	 */
	public static VideoScale get4K() {
		// 从技术上讲，“4K”意味着水平分辨率为4096像素。这是数字电影倡导联盟（Digital Cinema
		// Initiatives）所提出的分辨率。由于电影的纵横比不同，矩形屏幕的确切形状决定了电影的纵横比，所以其垂直分辨率并没有具体指定。
		// 因此，严格从技术上来说，“4K”不是超高清，超高清的分辨率为3840×2160。两者之间的像素区别仅13%，基本上看不到多大差异。绝大多数人都更喜欢“4K”这一名称表示超高清。
		return new VideoScale(3840, 2160);
	}

	/**
	 * 7680 x 4320
	 * 
	 * @return
	 */
	public static VideoScale get8K() {
		/*
		 * 8K的道理跟4K一样。如果你谈论的是显示屏，那么其水平像素和垂直像素均为4K显示屏的两倍：7680×4320。但8k视频源数量不多，
		 * 我们距离8K成为主流还很遥远，就不单独说了简单谈一下，Super
		 * Hi-Vision标准分辨率为7680×4320，总像素高达3300万个；为了更好的支持裸眼3D显示技术，Super
		 * Hi-Vision画面帧率统一提升至120fps；标准观看距离和视角模式亦有大幅提升。
		 */
		return new VideoScale(7680, 4320);
	}

	private int width;
	private int height;

	public VideoScale() {
	}

	/**
	 * 初始化视频缩放比例
	 * 
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
