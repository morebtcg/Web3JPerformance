package org.fisco.bcos.web3.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.web3.exception.Web3jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParser {
    private static final Logger logger = LoggerFactory.getLogger(ConfigParser.class);

    public static void mustExist(Map<String, Object> properties, String key) throws Web3jException {
        if (!properties.containsKey(key)) {
            throw new Web3jException(
                    Web3jException.ErrorCode.CONFIG_ERROR, "item: " + key + " not found");
        }
    }

    public static int parseInt(Map<String, Object> properties, String key, int defaultReturn) {
        Long res = ((Long) properties.get(key));

        if (res == null) {
            logger.info("{} has not set, default to {}", key, defaultReturn);
            return defaultReturn;
        }

        return res.intValue();
    }

    public static Long parseLong(Map<String, Object> properties, String key, Long defaultReturn) {
        Long res = ((Long) properties.get(key));

        if (res == null) {
            logger.info("{} has not set, default to {}", key, defaultReturn);
            return defaultReturn;
        }

        return res;
    }

    public static BigInteger parseBigInteger(
            Map<String, Object> properties, String key, BigInteger defaultReturn) {
        String str = parseString(properties, key, defaultReturn.toString());

        BigInteger res;
        if (str.startsWith("0x")) {
            res = new BigInteger(str.replaceFirst("0x", ""), 16);
        } else {
            res = new BigInteger(str);
        }

        return res;
    }

    public static BigDecimal parseBigDecimal(
            Map<String, Object> properties, String key, BigDecimal defaultReturn) {
        String str = parseString(properties, key, defaultReturn.toString());

        BigDecimal res;
        if (str.startsWith("0x")) {
            res = new BigDecimal(new BigInteger(str.replaceFirst("0x", ""), 16));
        } else {
            res = new BigDecimal(str);
        }
        return res;
    }

    public static String parseString(Map<String, Object> properties, String key)
            throws Web3jException {
        mustExist(properties, key);

        try {
            return (String) properties.get(key);
        } catch (Exception e) {
            throw new Web3jException(Web3jException.ErrorCode.CONFIG_ERROR, e.getMessage());
        }
    }

    public static String parseString(
            Map<String, Object> properties, String key, String defaultValue) {
        try {
            return parseString(properties, key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static List<String> parseArrayList(Map<String, Object> properties, String key)
            throws Web3jException {
        mustExist(properties, key);
        return (List<String>) properties.get(key);
    }
}
