package tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import model.ActorModel;
import model.Decision;

public class ActorExecutor {

	private static Logger log = Logger.getLogger(ActorExecutor.class.getName());

	/**
	 * 
	 * @param config
	 * @param am
	 * @param decision
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void RunAction(Properties config, ActorModel am, Decision decision)
			throws IOException, InterruptedException {

		ArrayList<String> commands = new ArrayList<String>();
		commands.add(config.getProperty("ACTOR_SCRIPT"));
		commands.add("set");
		commands.add("" + am.getGPIOPin());
		commands.add(decision.toString());
		log.info("Set GPIO:" + am.getGPIOPin() + " to " + decision.toString());
		log.info(commands.get(0) + " " + commands.get(1) + " " + commands.get(2) + " " + commands.get(3));
		// -----------
		ProcessBuilder builder = new ProcessBuilder(commands);

		builder.redirectErrorStream(true);

		Process shell = builder.start();

		// To capture output from the shell
		InputStream shellIn = shell.getInputStream();

		// Wait for the shell to finish and get the return code
		boolean isProcessOK = shell.waitFor(1, TimeUnit.MINUTES);
		if (isProcessOK) {
			log.info("GPIO-Actor Ergebnis : " + IOUtils.toString(shellIn));
		} else {
			log.info("Prozess konnte nicht korrekt beendet werdenn.");
			shell.destroy();
			log.info(commands.get(0) + " " + commands.get(1) + " " + commands.get(2) + " " + commands.get(3)
					+ " wurde gekillt");
		}

		shellIn.close();

	}
	
	/**
	 * 
	 * @param initScript
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void RunGPIOInit(String initScript) throws IOException, InterruptedException {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(initScript);
		log.info("Run : " + initScript);
		log.info(commands.get(0));
		// -----------
		ProcessBuilder builder = new ProcessBuilder(commands);
		builder.start();
		Thread.sleep(1000);
		log.info("Init Command ist pushed out...");

	}

	/**
	 * 
	 * @param config
	 * @param am
	 * @param decision
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void RunActionUnatended(String actorScript, String gpioPin, Decision decision)
			throws IOException, InterruptedException {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(actorScript);
		commands.add("set");
		commands.add(gpioPin);
		commands.add(decision.toString());
		log.info("Set GPIO:" + gpioPin + " to " + decision.toString());
		log.info(commands.get(0) + " " + commands.get(1) + " " + commands.get(2) + " " + commands.get(3));
		// -----------
		ProcessBuilder builder = new ProcessBuilder(commands);
		builder.start();
		Thread.sleep(1000);
		log.info("Command ist pushed out...");

	}

}
