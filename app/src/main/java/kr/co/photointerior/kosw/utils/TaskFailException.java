/*
 * @(#)TaskFailException.java
 */
package kr.co.photointerior.kosw.utils;

public class TaskFailException extends Exception {
	private static final long serialVersionUID = -3719022603830612259L;
	public TaskFailException(){}
	public TaskFailException(String message){
		super(message);
	}
	public TaskFailException(String message, Throwable t){
		super(message, t);
	}
}
