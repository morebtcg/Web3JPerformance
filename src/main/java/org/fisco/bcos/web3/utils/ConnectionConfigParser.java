package org.fisco.bcos.web3.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.web3.exception.Web3jException;

public class ConnectionConfigParser extends ConfigParser {
	private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
	private Service service;
	private List<Resource> resources = new ArrayList<>();

	public ConnectionConfigParser(Map<String, Object> properties) throws Web3jException {
		mustExist(properties, "service");
		try {
			service = new Service(properties.get("service"));

			if (properties.containsKey("resources")) {
				List<Object> resourcesConfigValue = (List<Object>) properties.get("resources");
				for (Object resourceConfig : resourcesConfigValue) {
					Resource resource = new Resource(resourceConfig);
					resources.add(resource);
				}
			}

		} catch (Exception e) {
			throw new Web3jException(Web3jException.ErrorCode.CONFIG_ERROR, e.getMessage());
		}
	}

	public Service getService() {
		return service;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public static class Service {
		private String url;
		private Long chainID;
		private Integer pollingInterval;
		private Boolean enableWs;

		public Service(Object properties) throws Web3jException {
			Map<String, Object> myProperties = (Map<String, Object>) properties;
			System.out.println("properties:" + myProperties);
			url = parseString(myProperties, "url");
			chainID = parseLong(myProperties, "chainID", null);
			pollingInterval = parseInt(myProperties, "pollingInterval", 100);
			enableWs = Boolean.parseBoolean(parseString(myProperties, "enableWs", "false"));
		}

		public String getUrl() {
			return url;
		}

		public Long getChainID() {
			return chainID;
		}

		public Integer getPollingInterval() {
			return pollingInterval;
		}

		public Boolean getEnableWs() {
			return enableWs;
		}
	}

	public static class Resource {
		private String name;
		private String address;
		private String abi;
		private Map<String, Object> properties;

		public Resource(Object properties) throws Web3jException {
			Map<String, Object> myProperties = (Map<String, Object>) properties;
			name = parseString(myProperties, "name");
			address = parseString(myProperties, "address").toLowerCase();
			abi = parseString(myProperties, "abi");
			this.properties = new HashMap<>();
			this.properties.put("name", name);
			this.properties.put("address", address);
			this.properties.put("abi", abi);
		}

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public String getAbi() {
			return abi;
		}

		public Map<String, Object> getProperties() {
			return properties;
		}

		@Override
		public String toString() {
			return "Resource{" + "name='" + name + '\'' + ", address='" + address + '\'' + ", abi='" + abi + '\''
					+ ", properties=" + properties + '}';
		}
	}
}
