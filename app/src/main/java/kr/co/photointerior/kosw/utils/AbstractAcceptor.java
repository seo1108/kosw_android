/*
 * @(#)AbstractAcceptor.java
 */
package kr.co.photointerior.kosw.utils;


/**
 * {@link Acceptor}의 1차 구현 미완성 클래스.
 */
public abstract class AbstractAcceptor implements Acceptor {
	private Object[] params;

	@Override
	public void setParams(Object... params) {
		this.params = params;
	}


	@Override
	public Object[] getParams() {
		return params;
	}

	@Override
	public void start() {

	}

	@Override
	public void start(Object... params) {

	}

	@Override
	public void stop() {

	}

	@Override
	public void stop(Object... params) {

	}

	@Override
	public void accept() {

	}

	@Override
	public void deny() {

	}

	@Override
	public void accept(Object... params) {

	}

	@Override
	public void deny(Object... params) {

	}
	
	@Override
	public void action() { }
	
	@Override
	public void action(Object... params) {}
}
