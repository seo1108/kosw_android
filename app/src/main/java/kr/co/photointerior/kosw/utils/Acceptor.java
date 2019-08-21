/*
 * @(#)Acceptor.java
 */
package kr.co.photointerior.kosw.utils;


/**
 * 작업 수행후 특정 작업을 처리하기 위한 인터페이스.
 */
public interface Acceptor {

	void setParams(Object... params);

	Object[] getParams();
	void start();
	void start(Object... params);
	void stop();
	void stop(Object... params);
	void accept(); 

	void deny();

	void accept(Object... params);

	void deny(Object... params);
	
	void action();
	void action(Object... params);
}
