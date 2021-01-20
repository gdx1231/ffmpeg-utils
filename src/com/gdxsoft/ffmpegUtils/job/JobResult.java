package com.gdxsoft.ffmpegUtils.job;

import java.util.HashMap;
import java.util.Map;

import com.gdxsoft.ffmpegUtils.ConvertResult;

public class JobResult {

	private Map<String, ConvertResult> results;

	public JobResult() {
		results = new HashMap<String, ConvertResult>();
	}

	public void addResult(String tag, ConvertResult result) {
		this.results.put(tag, result);
	}

	public ConvertResult getResult(String tag) {
		return this.results.containsKey(tag) ? this.results.get(tag) : null;
	}
	
	public ConvertResult getResultMp4() {
		return this.getResult("mp4");
	}
	
	public ConvertResult getResultTs() {
		return this.getResult("ts");
	}

	public ConvertResult getResultM3u8() {
		return this.getResult("m3u8");
	}

}
