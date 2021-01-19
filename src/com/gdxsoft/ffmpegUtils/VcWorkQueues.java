package com.gdxsoft.ffmpegUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VcWorkQueues implements Callable<String> {
	private int age;
	private static List<VcWorkQueues> queues;
	private static List<Future<String>> futures;

	public VcWorkQueues(int age) {
		super();
		this.age = age;
	}

	public String call() throws Exception {
		Thread.sleep(300);
		System.out.println("年龄是：" + age);
		return "返回值 年龄是：" + age;
	}

	public static void main(String[] args) throws InterruptedException {
		queues = new LinkedList<>();
		futures = new LinkedList<>();

		ThreadPoolExecutor executor = new ThreadPoolExecutor(7, 8, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1));

		for (int i = 0; i < 30; i++) {
			VcWorkQueues q = new VcWorkQueues(i);
			Future<String> a = executor.submit(q);// 1
			queues.add(q);
			futures.add(a);
		}

		// executor.submit(new MyCallable(10));// 10

		Thread.sleep(200);
		System.out.println("A:" + executor.getCorePoolSize());
		System.out.println("A:" + executor.getPoolSize());
		System.out.println("A:" + executor.getQueue().size());


		Thread.sleep(1000);
		System.out.println("B:" + executor.getCorePoolSize());
		System.out.println("B:" + executor.getPoolSize());
		System.out.println("B:" + executor.getQueue().size());

		try {
			queues.get(0).call();
		} catch (Exception e) {
		}
		executor.shutdown();
		
		System.out.println("B:" + executor.getCorePoolSize());
		System.out.println("B:" + executor.getPoolSize());
		System.out.println("B:" + executor.getQueue().size());

		try {
			queues.get(0).call();
		} catch (Exception e) {
		}
	}
}