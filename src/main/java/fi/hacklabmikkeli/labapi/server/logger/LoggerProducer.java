package fi.hacklabmikkeli.labapi.server.logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer for Logger object
 * 

 */
@Dependent
public class LoggerProducer {

  /**
   * Producer for Logger object
   * 
   * @param injectionPoint injection point
   * @return Logger
   */
	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
	  return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
	
}
