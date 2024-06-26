package org.fisco.bcos.web3.utils;

import com.moandjiezana.toml.Toml;
import java.io.FileNotFoundException;
import org.fisco.bcos.web3.exception.Web3jException;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ConfigUtils {
    public static Toml getToml(String fileName) throws Web3jException {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            return new Toml().read(resolver.getResource(fileName).getInputStream());
        } catch (IllegalStateException e) {
            throw new Web3jException(
                    Web3jException.ErrorCode.CONFIG_ERROR,
                    "Toml file " + fileName + " format error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            throw new Web3jException(
                    Web3jException.ErrorCode.CONFIG_ERROR,
                    "Toml file " + fileName + " not found: " + e.getMessage());
        } catch (Exception e) {
            throw new Web3jException(
                    Web3jException.ErrorCode.CONFIG_ERROR,
                    "Something wrong with parse " + fileName + ": " + e.getMessage());
        }
    }
}
