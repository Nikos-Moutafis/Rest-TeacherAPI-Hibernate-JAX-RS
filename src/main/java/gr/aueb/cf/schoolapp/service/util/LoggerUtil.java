package gr.aueb.cf.schoolapp.service.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        SLF4JBridgeHandler.install(); // Installs the bridge between (JUL - to_SLF) Handler
        Handler fileHandler;         // file handler for logging in file
        try {
            fileHandler = new FileHandler("cf.log", true);  // true for update the file, default is false and would
            fileHandler.setFormatter(new SimpleFormatter());              // create the file from beginning
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.addHandler(fileHandler);
    }

    private LoggerUtil() {}

    public static Logger getCurrentLogger() {
        return logger;
    }
}
