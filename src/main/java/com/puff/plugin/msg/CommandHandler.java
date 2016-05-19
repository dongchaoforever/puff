package com.puff.plugin.msg;

public interface CommandHandler {

	public byte getNameSpace();

	public void handle(Command command);

}
