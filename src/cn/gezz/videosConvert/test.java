package cn.gezz.videosConvert;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

public class test {
	public static FFmpeg ffmpeg;
	public static FFprobe ffprobe;
	static {
		try {
			ffmpeg = new FFmpeg("e:/Guolei/ffmpeg/bin/ffmpeg.exe");
			ffprobe = new FFprobe("e:/Guolei/ffmpeg/bin/ffprobe.exe");
		} catch (IOException e) {
		}
	}

	public test() {
	}

	private void exec(FFmpegBuilder builder, ProgressListener listener) {
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		FFmpegJob job = executor.createJob(builder, listener);

		job.run();
	}

	public void convert() throws IOException {

		String sourceFile = "d:/360Rec/a.mp4";
		FFmpegProbeResult in = ffprobe.probe(sourceFile);

		String outFile = "d:/360Rec/test.mp4";
		String outFile1 = "d:/360Rec/test.ts";
		String outFile2 = "d:/360Rec/test.m3u8";
		String logo = "d:/360Rec/logo.png";

		FFmpegFormat format = in.getFormat();
		System.out.format("%nFile: '%s' ; Format: '%s' ; Duration: %.3fs", format.filename, format.format_long_name,
				format.duration);
		FFmpegStream stream = in.getStreams().get(0);
		System.out.format("%nCodec: '%s' ; Width: %dpx ; Height: %dpx \n", stream.codec_long_name, stream.width,
				stream.height);

		FFmpegBuilder builder = new FFmpegBuilder();
		builder.setInput(in); // Filename, or a FFmpegProbeResult
		builder.addInput(logo);
		builder.overrideOutputFiles(true); // Override the output if it exists

		FFmpegOutputBuilder out1 = builder.addOutput(outFile);
		// Filename for the destination
		// .setFormat("mp4") // Format is inferred from filename, or can be set
		out1.disableSubtitle(); // No subtiles
		out1.setVideoCodec("h264_nvenc"); // Video using x264
		// .setVideoFrameRate(24, 1) // at 24 frames per second
		// .setVideoResolution(480, -1) // at 640x480 resolution
		// .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use
		// experimental specs
		out1.setVideoBitRate(2 * 1204 * 1024);
		out1.done();

//		FFmpegOutputBuilder out2 = builder.addOutput(outFile1);
//		out2.disableSubtitle(); // No subtiles
//		out2.setVideoCodec("h264_nvenc"); // Video using x264
//		out2.setVideoBitRate(1204 * 1024);
//		out2.done();

		builder.addExtraArgs("-hwaccel", "cuda");
		// builder.setComplexFilter(
		// "\"[1:v]scale=100:-1[logo];[0:v][logo]overlay=main_w-overlay_w-10:10\"");

		builder.setComplexFilter(
				"\"[0:v]scale=480:-1[videoResized];[1:v]scale=100:-1[logo];[videoResized][logo]overlay=main_w-overlay_w-10:10\"");
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		// Run a one-pass encode

		long t0 = System.currentTimeMillis();

		FFmpegJob job = executor.createJob(builder, new ProgressListener() {

			// Using the FFmpegProbeResult determine the duration of the input
			final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

			@Override
			public void progress(Progress progress) {
				double percentage = progress.out_time_ns / duration_ns;

				// Print out interesting information about the progress
				System.out.println(String.format("[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
						percentage * 100, progress.status, progress.frame,
						FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS), progress.fps.doubleValue(),
						progress.speed));
			}
		});

		// job.run();

		FFmpegBuilder builder1 = new FFmpegBuilder();
		builder1.addInput(outFile);
		FFmpegOutputBuilder out2 = builder1.addOutput(outFile1);
		out2.setVideoCodec("copy");
		out2.addExtraArgs("-bsf:v", "h264_mp4toannexb");

		FFmpegJob job1 = executor.createJob(builder1, new ProgressListener() {

			// Using the FFmpegProbeResult determine the duration of the input
			final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

			@Override
			public void progress(Progress progress) {
				double percentage = progress.out_time_ns / duration_ns;

				// Print out interesting information about the progress
				System.out.println(String.format("[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
						percentage * 100, progress.status, progress.frame,
						FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS), progress.fps.doubleValue(),
						progress.speed));
			}
		});

		// job1.run();

		FFmpegBuilder builder3 = new FFmpegBuilder();
		builder3.addInput(outFile1);
		FFmpegOutputBuilder out3 = builder3.addOutput(outFile1 + "%03d.ts");
		out3.setVideoCodec("copy");
		out3.addExtraArgs("-map", "0", "-f", "segment", "-segment_list", outFile2, "-segment_time", "5");

		FFmpegJob job3 = executor.createJob(builder3, new ProgressListener() {

			// Using the FFmpegProbeResult determine the duration of the input
			final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

			@Override
			public void progress(Progress progress) {
				double percentage = progress.out_time_ns / duration_ns;

				// Print out interesting information about the progress
				System.out.println(String.format("[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
						percentage * 100, progress.status, progress.frame,
						FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS), progress.fps.doubleValue(),
						progress.speed));
			}
		});

		job3.run();
		long t1 = System.currentTimeMillis();
		System.out.println("done " + (t1 - t0) / 1000.0);

	}

	public static void main(String[] args) {
		test vc = new test();
		try {
			vc.convert();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
