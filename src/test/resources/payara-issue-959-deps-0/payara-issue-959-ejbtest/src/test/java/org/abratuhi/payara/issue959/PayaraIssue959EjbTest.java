package org.abratuhi.payara.issue959;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.glassfish.ejb.embedded.EJBContainerProviderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

public class PayaraIssue959EjbTest {
	private static final Logger LOG = Logger.getLogger(PayaraIssue959EjbTest.class);

	private EJBContainer container;

	private boolean isContainerAlreadyStarted = isExisting();

	@Test
	public void testDeployAndExec() {

	}

	protected String getMavenRepo() {
		String mavenRepo = System.getenv("MAVEN_REPO");
		if (StringUtils.isEmpty(mavenRepo)) {
			LOG.error("You need to configure MAVEN_REPO first!");
			System.exit(1);
		}
		return mavenRepo;
	}

	@Before
	public void setUp() {
		if (Logger.getRootLogger().getAppender("stdout") == null ) {
			Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p %d [%c] - %C{1}.%M: %m%n")));
		} else {
			Logger.getRootLogger().getAppender("stdout").setLayout(new PatternLayout("%-5p %d [%c] - %C{1}.%M: %m%n"));
		}
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final Properties props = new Properties();
		File[] modules = new File[]{
				new File(getMavenRepo() + "/org/abratuhi/payara-issue-959-ejb/1.0-SNAPSHOT/payara-issue-959-ejb-1.0-SNAPSHOT.jar"),
		};

		// check whether all the modules exist
		for (File file : modules) {
			if (!file.exists()) {
				throw new IllegalArgumentException("Module " + file.getAbsolutePath() + " does not exist!");
			}
		}

		props.put(EJBContainer.MODULES, modules);
		props.put(EJBContainer.APP_NAME, "ejb-app");

		container = EJBContainer.createEJBContainer(props);
	}

	@After
	public void tearDown() {
//		if (isContainerAlreadyStarted)
//			return;
		forceTearDown();
	}

	public void forceTearDown() {
		if (null != container) {
			container.close();
		}
	}

	private boolean isExisting() {
		try {
			Field f = EJBContainerProviderImpl.class.getDeclaredField("container");
			f.setAccessible(true);
			return f.get(null) != null;
		} catch (Exception e) {
		}
		return false;
	}
}
