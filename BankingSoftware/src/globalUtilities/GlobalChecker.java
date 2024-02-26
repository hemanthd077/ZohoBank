package globalUtilities;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import handleError.CustomException;
import helper.ColoredConsoleHandler;

public class GlobalChecker {
	//null check
	public static void checkNull(Object input) throws CustomException{
		if(input == null){
			throw new CustomException("The NULL Pointer Exception Occured, Input is NULL");
		}
	}
	
	public static void loggerHandler() throws CustomException {
		try {
			Logger logger = Logger.getGlobal();
			checkNull(logger);
			ColoredConsoleHandler coloredConsoleHandler = new ColoredConsoleHandler();
	        logger.addHandler(coloredConsoleHandler);
			
			coloredConsoleHandler.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					
					return record.getMessage();
				}
			});
	        logger.setUseParentHandlers(false);
		}
	    catch (SecurityException e) {
			throw new CustomException("Error occured in loggerHandler",e);
		}
	}
}
