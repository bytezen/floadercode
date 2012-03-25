package org.mt4j.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;


public class JavaLogger implements ILogger {
	private Logger logger;

	public JavaLogger(){	}
	
	private JavaLogger(String name){
		this.logger = Logger.getLogger(name);
//		ConsoleHandler cons = new ConsoleHandler(); //TODO already done per default?
//		this.logger.addHandler(cons);
	}
	
	@Override
	public void setLevel(int level) {
		switch (level) {
		case OFF:
			this.logger.setLevel(Level.OFF); 
			break;
		case ALL:
			this.logger.setLevel(Level.ALL); 
			break;
		case INFO:
			this.logger.setLevel(Level.INFO); 
			break;
		case DEBUG:
			this.logger.setLevel(Level.CONFIG);  //TODO which level matches debug?
			break;
		case WARN:
			this.logger.setLevel(Level.WARNING); 
			break;
		case ERROR:
			this.logger.setLevel(Level.SEVERE); 
			break;
		default:
			break;
		}
	}

	@Override
	public void info(Object message) {
		logger.info(message.toString());
	}

	@Override
	public void debug(Object message) {
		logger.info(message.toString());
	}

	@Override
	public void warn(Object message) {
		logger.warning(message.toString());
	}

	@Override
	public void error(Object message) {
		logger.severe(message.toString());
	}

	@Override
	public ILogger createNew(String name) {
		return new JavaLogger(name);
	}

	@Override
	public int getLevel() {
		Level level = this.logger.getLevel();
		if (level.equals(Level.OFF)){
			return ILogger.OFF;
		}else if (level.equals(Level.ALL)){
			return ILogger.ALL;
		}else if (level.equals(Level.INFO)){
			return ILogger.INFO;
		}else if (level.equals(Level.CONFIG)){
			return ILogger.DEBUG;
		}else if (level.equals(Level.WARNING)){
			return ILogger.WARN;
		}else if (level.equals(Level.SEVERE)){
			return ILogger.ERROR;
		}else{
			return -1;
		}
	}

}
