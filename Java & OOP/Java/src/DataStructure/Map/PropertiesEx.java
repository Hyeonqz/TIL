package DataStructure.Map;

import java.util.Properties;

public class PropertiesEx {
	public static void main(String[] args) throws Exception {

		Properties properties = new Properties();
		properties.load(PropertiesEx.class.getResourceAsStream("database.properties"));

		String driver = properties.getProperty("driver");
		String url = properties.getProperty("url");
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");
		String admin = properties.getProperty("admin");

		System.out.println("driver : " +driver);
		System.out.println("url : " +url);
		System.out.println("username : " +username);
		System.out.println("password : " +password);
		System.out.println("admin : " +admin);

	}
}
