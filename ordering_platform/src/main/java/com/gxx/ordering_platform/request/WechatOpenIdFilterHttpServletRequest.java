package com.gxx.ordering_platform.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WechatOpenIdFilterHttpServletRequest extends HttpServletRequestWrapper {

	private byte[] body;
	private boolean open = false;
	
	public WechatOpenIdFilterHttpServletRequest(HttpServletRequest request, byte[] body) {
		super(request);
		this.body = body;
	}
	
	public ServletInputStream getInputStream() throws IOException {
		if (open) {
			throw new IllegalStateException("Cannot re-open input stream!");
		}
		open = true;
		return new ServletInputStream() {
			
			private int offset = 0;
			
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				if (offset >= body.length) {
					return -1;
				}
				int n = body[offset] & 0xff;
				offset++;
				return n;
			}
			
			@Override
			public void setReadListener(ReadListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean isFinished() {
				// TODO Auto-generated method stub
				return offset >= body.length;
			}
		};
	}
	
	public BufferedReader getReader() throws IOException {
		if (open) {
			throw new IllegalStateException("Cannot re-open reader!");
		}
		open = true;
		return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
	}
}
