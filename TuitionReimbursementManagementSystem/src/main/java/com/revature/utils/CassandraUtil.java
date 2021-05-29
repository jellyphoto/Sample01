package com.revature.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

public class CassandraUtil {
	private static final Logger log = LogManager.getLogger(CassandraUtil.class);
	private static CassandraUtil instance = null;
	
	private CqlSession session = null;
	
	private CassandraUtil() {
		log.trace("Establishing connection with Cassandra");
		DriverConfigLoader loader = DriverConfigLoader.fromClasspath("application.conf");
		try {
			log.trace("Attemption to build session");	//VERIFY
			this.session = CqlSession.builder().withConfigLoader(loader).build();
			//withKeyspace("trms_ks") may allow one to skip specifying keyspace in queries
			//does this create the keyspace if it DNE? NO (appearantly)
			log.trace("Session building attempt should have succeeded");
		} catch(Exception e) {
			log.error("Method threw exception: "+e);
			for(StackTraceElement s : e.getStackTrace()) {
				log.warn(s);
			}
			throw e;
		}
	}
	
	public static synchronized CassandraUtil getInstance() {
		if(instance == null) {
			instance = new CassandraUtil();
		}
		return instance;
	}
	
	public CqlSession getSession() {
		return session;
	}
	
	public boolean assertKeyspaceExistence() {
		StringBuilder sb = new StringBuilder("create keyspace if not exists")
									.append(" trms_ks")
									.append(" with replication = {")
									.append("'class':'SimpleStrategy','replication_factor':1};");
		
		try{
			log.trace("Attempting to verify existence of keyspaces");
			this.session.execute(sb.toString());
			log.trace("Keyspace existence verification success");
		} catch(Exception e) {
			log.error("Method threw exception: "+e);
			for(StackTraceElement s : e.getStackTrace()) {
				log.warn(s);
			}
			//throw e;
			return false;
		}		
		return true;
	}
}
